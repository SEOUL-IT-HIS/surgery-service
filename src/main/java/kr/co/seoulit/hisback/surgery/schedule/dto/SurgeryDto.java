package kr.co.seoulit.hisback.surgery.schedule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 스케줄 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryDto {
    private String surgeryId;
    private String patientId;
    private String surgeonId;
    private String anesthesiologistId;
    private String nurseId;
    private String roomCode;
    private LocalDate surgeryDt;
    private String statusCd;
    private String progressCd;
    private String cancelReasonCd;
    private String surgTypeCd;
    private String surgeryName;
    private String emergencyYn;
    private LocalDate actualStartDt;
    private LocalDate actualEndDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
