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

    /**
     * 오더 수락 시 수술을 만든다 (surgeryorder → schedule 진입점)
     *
     * <p>수술실이 정해져 요청이 받아들여진 것이므로 <b>예약(01)</b>에서 시작한다.
     * 요청접수(00)를 거치지 않는다 — 그 단계는 이제 SURGERY_ORDER 가 담당한다.</p>
     *
     * <p><b>왜 오더 서비스가 직접 저장하지 않고 여기로 오는가</b> — 수술을 만들 때
     * 상태변경 이력을 함께 남겨야 하는데(SL2-282), 그 로직이 이 클래스에 있다.
     * 저장을 두 곳에서 하면 한쪽만 이력을 빠뜨리게 된다.</p>
     */
    SurgeryDto createScheduledSurgery(SurgeryDto request);

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

    /**
     * SL2-170: 수술실 배정 현황 조회
     *
     * <p>기간·수술실·상태로 걸러 <b>수술 건 단위로 평평하게</b> 돌려준다.</p>
     *
     * <p><b>수술실 기준으로 묶지 않은 이유</b>(§21.7 판단 근거 기록) —
     * 이 정보를 보는 것이 우리 화면만이 아니다. 진료·응급이 자기가 올린 요청이 어떻게
     * 됐는지 물어볼 때, 수술실로 묶인 응답은 받아서 다시 펼쳐야 한다. 그쪽의 관심은
     * 방이 아니라 환자와 수술 건이다. 서비스 간 교환이 REST 인 이상(§21.3) 우리만
     * 중첩 구조를 쓰면 상대가 우리 응답 모양을 따로 배워야 한다.</p>
     *
     * <p>방별로 묶어 보고 싶은 화면은 받아서 묶으면 되고, <b>배정이 없는 빈 방</b>까지
     * 필요하면 {@code GET /api/surgery/monitoring/rooms}(SL2-287)를 함께 부른다 —
     * 그쪽이 이미 수술실 전체를 공실 여부와 함께 돌려준다.</p>
     *
     * @param roomCode 수술실 코드. null 이면 전체(미배정 건도 포함된다)
     * @param statusCd 수술 상태. null 이면 전체(취소 건도 섞인다)
     * @param patientId 환자 식별자. null 이면 전체. 이름이 아니라 식별자로만 찾는다(§21.9)
     * @param surgeonId 집도의 식별자. null 이면 전체
     * @param fromDt 수술일 시작. null 이면 하한 없음
     * @param toDt 수술일 종료. null 이면 상한 없음
     */
    PageResponse<SurgeryDto> getAssignments(
            String roomCode,
            String statusCd,
            String patientId,
            String surgeonId,
            LocalDate fromDt,
            LocalDate toDt,
            Pageable pageable);

    // SL2-15 일괄 배정은 SurgeryOrderService.assignOrder 로 옮겼다.

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
