package kr.co.seoulit.hisback.surgery.room.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술장비 응답 DTO (가이드 §11.3: ApiResponse.data 로 감싸 응답)
 * <p>OperatingRoomDto와 동일하게 프론트 타입과 필드명을 camelCase로 맞춘다.
 * 컨트롤러의 출고반입(PATCH /inout)이 {@code inoutCd} 키를 사용하는 것과도 일치한다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgicalEquipmentDto {
    private String equipmentId;
    private String roomCode;
    private String equipmentName;
    private String statusCd;
    private String inoutCd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
