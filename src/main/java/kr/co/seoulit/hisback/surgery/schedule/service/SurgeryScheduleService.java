package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryStatusHistoryDto;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * 수술 스케줄링 서비스 인터페이스 (구현체는 SurgeryScheduleServiceImpl)
 */
public interface SurgeryScheduleService {
    List<SurgeryDto> getSchedules(LocalDate surgeryDt);

    SurgeryDto getSchedule(String surgeryId);

    /**
     * SL2-282: 상태변경 이력 조회.
     *
     * <p>이력을 만드는 메서드는 따로 두지 않는다 — 상태를 바꾸는 메서드들이 알아서 남긴다.
     * 이력을 직접 쓸 수 있으면 이력으로서의 신뢰를 잃는다.</p>
     *
     * @param surgeryId 수술 식별자. 없는 수술이면 404 SUR035.
     * @param statusType STATUS 또는 PROGRESS. null·빈값이면 두 종류를 모두 돌려준다.
     */
    List<SurgeryStatusHistoryDto> getStatusHistory(String surgeryId, String statusType);

    /** SL2-36: 정규 수술 등록 */
    SurgeryDto registerSchedule(SurgeryDto request);

    /** SL2-44: 응급 수술은 일정 충돌 검사 없이 우선 배정 (호출부에서 emergencyYn="Y" 세팅) */
    SurgeryDto registerEmergencySchedule(SurgeryDto request);

    /** SL2-37: 일정/배정 수정 */
    SurgeryDto updateSchedule(String surgeryId, SurgeryDto request);

    /** SL2-33: 물리 삭제 대신 상태 전이(취소)로 표현 */
    SurgeryDto cancelSchedule(String surgeryId, String cancelReasonCd);

    /** SL2-13: 집도의 배정 */
    SurgeryDto assignSurgeon(String surgeryId, String surgeonId);

    /** SL2-15: 수술실 배정 */
    SurgeryDto assignRoom(String surgeryId, String roomCode);

    /** SL2-43: 마취의 배정 */
    SurgeryDto assignAnesthesiologist(String surgeryId, String anesthesiologistId);

    /** SL2-63: 간호사 배정 */
    SurgeryDto assignNurse(String surgeryId, String nurseId);

    /** SL2-225: 배정 대기 목록 (status_cd = 요청접수). 응급 건이 먼저 나온다. */
    /**
     * SL2-225/235/236: 배정 대기 목록 (검색 + 페이지 단위)
     *
     * <p>전체 목록을 돌려주던 것을 페이지 단위로 바꿨다. 요청이 쌓이면 한 번에 다 내려받게 되고,
     * 배정 담당자가 찾는 것은 보통 특정 환자나 특정 날짜의 건이다.</p>
     *
     * <p>검색 조건은 모두 선택이다. 아무것도 넣지 않으면 요청접수(00) 전체가 나온다.</p>
     *
     * @param emergencyYn 'Y'/'N'. null 이면 전체
     * @param patientId 환자 식별자. null·공백이면 전체
     * @param fromDt 희망일 시작. null 이면 하한 없음
     * @param toDt 희망일 종료. null 이면 상한 없음
     */
    PageResponse<SurgeryDto> getRequestedSchedules(
            String emergencyYn, String patientId, LocalDate fromDt, LocalDate toDt, Pageable pageable);

    /**
     * SL2-15: 수술 배정 — 수술실·마취의·간호사(및 조정 예정일)를 한 번에 채우고 예약으로 전이한다.
     *
     * <p>환자·집도의는 요청 주체(진료·응급실)가 확정한 값이라 여기서 바꾸지 않는다.
     * 개별 배정 API(/surgeon, /room, /anesthesiologist, /nurse)는 배정 후 부분 변경용으로 남는다.</p>
     */
    SurgeryDto assignSurgery(String surgeryId, SurgeryDto request);

    /** 수술 시작 — 예약→진행중 전이 + 실제 시작일 기록 */
    SurgeryDto startSurgery(String surgeryId);

    /** SL2-40: 금일 수술현황 대시보드 (스케줄 목록 재사용) */
    List<SurgeryDto> getTodaySchedules();

    /** SL2-39: 당일 실시간 진행상태 변경 (status_cd와 별도 트랙) */
    SurgeryDto updateProgress(String surgeryId, String progressCd);

    /**
     * 수술 완료 — 진행중→완료 전이 + 실제 종료일 기록.
     * 물리 삭제가 아닌 상태 전이라 §21.6 원칙에도 부합한다.
     *
     * <p>SL2-72(수납 청구 연계)는 아직 붙어 있지 않다 — BillingServiceClient 로 REST 호출 예정(§21.3).</p>
     */
    SurgeryDto completeSurgery(String surgeryId);
}
