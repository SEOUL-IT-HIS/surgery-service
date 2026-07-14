package kr.co.seoulit.hisback.surgery.schedule.dto;

import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술 스케줄 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryDto {

    private String surgeryId;
    private String patientMpiId;
    private String roomCode;
    private String surgeonId;
    private String anesthesiologistId;
    private String surgeryName;
    private LocalDateTime scheduledAt;
    private SurgeryStatus status;
    private String statusLabel;
    private boolean emergency;
    private LocalDateTime actualStartAt;
    private LocalDateTime actualEndAt;

    /** 엔티티 → DTO */
    public static SurgeryDto from(Surgery s) {
        return SurgeryDto.builder()
                .surgeryId(s.getSurgeryId())
                .patientMpiId(s.getPatientMpiId())
                .roomCode(s.getRoomCode())
                .surgeonId(s.getSurgeonId())
                .anesthesiologistId(s.getAnesthesiologistId())
                .surgeryName(s.getSurgeryName())
                .scheduledAt(s.getScheduledAt())
                .status(s.getStatus())
                .statusLabel(s.getStatus() != null ? s.getStatus().getLabel() : null)
                .emergency(s.isEmergency())
                .actualStartAt(s.getActualStartAt())
                .actualEndAt(s.getActualEndAt())
                .build();
    }

    /** 신규 등록용 DTO → 엔티티 (surgeryId는 엔티티 @PrePersist에서 UUID로 생성) */
    public Surgery toEntity() {
        return Surgery.builder()
                .patientMpiId(patientMpiId)
                .roomCode(roomCode)
                .surgeonId(surgeonId)
                .anesthesiologistId(anesthesiologistId)
                .surgeryName(surgeryName)
                .scheduledAt(scheduledAt)
                .status(status != null ? status : SurgeryStatus.SCHEDULED)
                .emergency(emergency)
                .build();
    }
}
