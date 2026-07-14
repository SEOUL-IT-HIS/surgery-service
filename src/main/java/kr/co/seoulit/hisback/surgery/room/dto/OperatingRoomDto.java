package kr.co.seoulit.hisback.surgery.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoomStatus;

/**
 * 수술실 DTO
 */
public class OperatingRoomDto {

    private OperatingRoomDto() {
    }

    /** SL2-7: 수술실 추가 요청 */
    public record CreateRequest(
            @NotBlank(message = "수술실 코드는 필수입니다.") String roomCode,
            @NotBlank(message = "수술실 이름은 필수입니다.") String roomName
    ) {
    }

    /** SL2-30: 수술실 정보 수정 요청 */
    public record UpdateRequest(
            @NotBlank(message = "수술실 이름은 필수입니다.") String roomName
    ) {
    }

    /** SL2-8: 수술실 상태 변경(제거) 요청 - 물리 삭제 대신 상태 전이 */
    public record StatusUpdateRequest(
            @NotNull(message = "변경할 상태 코드는 필수입니다.") OperatingRoomStatus statusCd
    ) {
    }

    /** SL2-6: 수술실 조회 응답 */
    public record Response(
            String roomCode,
            String roomName,
            OperatingRoomStatus statusCd,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static Response from(OperatingRoom entity) {
            return new Response(
                    entity.getRoomCode(),
                    entity.getRoomName(),
                    entity.getStatusCd(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
            );
        }
    }
}
