package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;

/**
 * 수술 스케줄링 서비스 인터페이스 (구현체는 SurgeryScheduleServiceImpl)
 */
public interface SurgeryScheduleService {
    List<SurgeryDto> getSchedules(LocalDate surgeryDt);

    SurgeryDto getSchedule(String surgeryId);

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

    /** SL2-40: 금일 수술현황 대시보드 (스케줄 목록 재사용) */
    List<SurgeryDto> getTodaySchedules();

    /** SL2-39: 당일 실시간 진행상태 변경 (status_cd와 별도 트랙) */
    SurgeryDto updateProgress(String surgeryId, String progressCd);

    /**
     * SL2-72: 수술을 완료 상태로 전이하고, 수납(Billing)이 구독하는 수술완료 이벤트를 발행한다.
     * 물리 삭제가 아닌 상태 전이라 §21.6 원칙에도 부합한다.
     */
    SurgeryDto completeSurgery(String surgeryId);
}
