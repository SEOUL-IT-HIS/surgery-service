package kr.co.seoulit.hisback.surgery.room.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술실 응답 DTO (가이드 §11.3: ApiResponse.data 로 감싸 응답)
 * <p>프론트 OperatingRoom 타입(hisfrontend/src/features/surgery/types.ts)과 필드명을
 * 그대로 맞췄다(camelCase).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatingRoomDto {
    private String roomCode;
    private String roomName;
    private String statusCd;
    private String turnoverCd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
