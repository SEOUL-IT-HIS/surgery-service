package kr.co.seoulit.hisback.surgery.room.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술장비 요청/응답 DTO (가이드 §11.3: ApiResponse.data 로 감싸 응답)
 * <p>OperatingRoomDto와 동일하게 프론트 타입과 필드명을 camelCase로 맞춘다.
 * 컨트롤러의 출고반입(PATCH /inout)이 {@code inoutCd} 키를 사용하는 것과도 일치한다.</p>
 *
 * <p>장비명만 필수로 둔다(SL2-140). equipmentId 는 수정 시 경로변수가 우선이라 제약을 걸면
 * 안 되고, roomCode 는 소속 수술실이 미정인 장비가 있을 수 있어 선택이다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgicalEquipmentDto {

    private String equipmentId;

    private String roomCode;

    @NotBlank
    private String equipmentName;

    private String statusCd;
    private String inoutCd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
