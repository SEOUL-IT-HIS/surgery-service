package kr.co.seoulit.hisback.surgery.client;

import java.time.LocalDateTime;

/**
 * 병동관리(WARD) 서비스 연동 클라이언트
 * <p>SurgeryScheduleService에서 수술 스케줄 등록/응급등록 시 병동에 통보하는 용도로 사용 중.
 * 실제 HTTP 연동(FeignClient/RestClient 등) 구현체는 별도 Impl 클래스에서 채워야 한다.</p>
 */
public interface WardServiceClient {

    /**
     * 수술 예약 사실을 병동관리 서비스에 통보한다 (전/후 병상 계획 수립용).
     *
     * @param surgeryId    수술 고유번호 (UUID)
     * @param patientMpiId 환자 마스터 식별자
     * @param scheduledAt  수술 예정일시
     */
    void notifySurgeryScheduled(String surgeryId, String patientMpiId, LocalDateTime scheduledAt);
}
