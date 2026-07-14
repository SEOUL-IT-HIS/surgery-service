package kr.co.seoulit.hisback.surgery.room.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipmentStatus;

/**
 * 수술장비 DTO
 * <p>현재 SL2-9(조회)/SL2-10(추가) 범위만 포함. 제거/출고반입/수정 관련 요청 타입은
 * 해당 스토리 착수 시 추가한다.</p>
 */
public class SurgicalEquipmentDto {

    private SurgicalEquipmentDto() {
    }

    /** SL2-10: 수술장비 추가 요청 */
    public record CreateRequest(
            String roomCode,
            @NotBlank(message = "수술장비 이름은 필수입니다.") String equipmentName
    ) {
    }

    /** SL2-9: 수술장비 조회 응답 */
    public record Response(
            String equipmentId,
            String roomCode,
            String equipmentName,
            SurgicalEquipmentStatus statusCd,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static Response from(SurgicalEquipment entity) {
            return new Response(
                    entity.getEquipmentId(),
                    entity.getRoomCode(),
                    entity.getEquipmentName(),
                    entity.getStatusCd(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
            );
        }
    }
}
