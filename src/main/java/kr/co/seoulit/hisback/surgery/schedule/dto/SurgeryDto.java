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

    private Long surgeryId;
    private String patientMpiId;
    private String operatingRoom;
    private String surgeonId;
    private String anesthesiologistId;
    private String surgeryName;
    private LocalDateTime scheduledDt;
    private SurgeryStatus status;
    private String statusLabel;
    private boolean emergency;
    private LocalDateTime actualStartDt;
    private LocalDateTime actualEndDt;

    /** 엔티티 → DTO */
    public static SurgeryDto from(Surgery s) {
        return SurgeryDto.builder()
                .surgeryId(s.getSurgeryId())
                .patientMpiId(s.getPatientMpiId())
                .operatingRoom(s.getOperatingRoom())
                .surgeonId(s.getSurgeonId())
                .anesthesiologistId(s.getAnesthesiologistId())
                .surgeryName(s.getSurgeryName())
                .scheduledDt(s.getScheduledDt())
                .status(s.getStatus())
                .statusLabel(s.getStatus() != null ? s.getStatus().getLabel() : null)
                .emergency(s.isEmergency())
                .actualStartDt(s.getActualStartDt())
                .actualEndDt(s.getActualEndDt())
                .build();
    }

    /** 신규 등록용 DTO → 엔티티 */
    public Surgery toEntity() {
        return Surgery.builder()
                .patientMpiId(patientMpiId)
                .operatingRoom(operatingRoom)
                .surgeonId(surgeonId)
                .anesthesiologistId(anesthesiologistId)
                .surgeryName(surgeryName)
                .scheduledDt(scheduledDt)
                .status(status != null ? status : SurgeryStatus.SCHEDULED)
                .emergency(emergency)
                .build();
    }
}
