package kr.co.seoulit.hisback.surgery.client;

import java.time.LocalDateTime;

/**
 * 병동관리(WARD) 서비스 연동 클라이언트
 * <p>수술 일정이 확정되면 관련 병동에 통보한다. (FR-SUR-002 사후조건)</p>
 */
public interface WardServiceClient {

    /**
     * 수술 예약 사실을 병동관리 서비스로 통보한다.
     *
     * @param surgeryId    수술 고유번호
     * @param patientMpiId 환자 마스터 식별자
     * @param scheduledDt  수술 예정일시
     */
    void notifySurgeryScheduled(Long surgeryId, String patientMpiId, LocalDateTime scheduledDt);
}
