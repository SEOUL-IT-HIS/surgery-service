package kr.co.seoulit.hisback.surgery.room.dto;

import kr.co.seoulit.hisback.surgery.room.entity.EquipmentStatus;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술장비 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgicalEquipmentDto {

    private Long id;
    private String equipmentCode;
    private String equipmentName;
    private String category;
    private String operatingRoom;
    private EquipmentStatus status;
    private String statusLabel;
    private String lastMovementType;
    private LocalDateTime lastMovementDt;
    private boolean active;

    public static SurgicalEquipmentDto from(SurgicalEquipment e) {
        return SurgicalEquipmentDto.builder()
                .id(e.getId())
                .equipmentCode(e.getEquipmentCode())
                .equipmentName(e.getEquipmentName())
                .category(e.getCategory())
                .operatingRoom(e.getOperatingRoom())
                .status(e.getStatus())
                .statusLabel(e.getStatus() != null ? e.getStatus().getLabel() : null)
                .lastMovementType(e.getLastMovementType())
                .lastMovementDt(e.getLastMovementDt())
                .active(e.isActive())
                .build();
    }

    public SurgicalEquipment toEntity() {
        return SurgicalEquipment.builder()
                .equipmentCode(equipmentCode)
                .equipmentName(equipmentName)
                .category(category)
                .operatingRoom(operatingRoom)
                .status(status != null ? status : EquipmentStatus.AVAILABLE)
                .active(true)
                .build();
    }
}
