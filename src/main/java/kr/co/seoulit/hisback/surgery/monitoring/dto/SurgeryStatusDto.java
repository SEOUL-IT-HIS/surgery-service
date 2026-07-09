package kr.co.seoulit.hisback.surgery.monitoring.dto;

import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술 진행상태/대시보드 DTO (SL2-40 금일 수술 현황)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryStatusDto {

    private Long surgeryId;
    private String patientMpiId;
    private String operatingRoom;
    private String surgeonId;
    private SurgeryStatus status;
    private String statusLabel;
    private LocalDateTime scheduledDt;
    private LocalDateTime actualStartDt;
    private LocalDateTime actualEndDt;
    private LocalDateTime updatedAt;

    /** 직전 수술 종료 후 본 수술 시작까지의 턴오버 타임(분). 계산 불가 시 null (SL2-50) */
    private Long turnoverMinutes;

    public static SurgeryStatusDto from(Surgery s) {
        return SurgeryStatusDto.builder()
                .surgeryId(s.getSurgeryId())
                .patientMpiId(s.getPatientMpiId())
                .operatingRoom(s.getOperatingRoom())
                .surgeonId(s.getSurgeonId())
                .status(s.getStatus())
                .statusLabel(s.getStatus() != null ? s.getStatus().getLabel() : null)
                .scheduledDt(s.getScheduledDt())
                .actualStartDt(s.getActualStartDt())
                .actualEndDt(s.getActualEndDt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
