package kr.co.seoulit.hisback.surgery.room.dto;

import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수술실 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingRoomDto {

    private Long id;
    private String roomCode;
    private String roomName;
    private String roomType;
    private RoomStatus status;
    private String statusLabel;
    private boolean active;

    public static OperatingRoomDto from(OperatingRoom r) {
        return OperatingRoomDto.builder()
                .id(r.getId())
                .roomCode(r.getRoomCode())
                .roomName(r.getRoomName())
                .roomType(r.getRoomType())
                .status(r.getStatus())
                .statusLabel(r.getStatus() != null ? r.getStatus().getLabel() : null)
                .active(r.isActive())
                .build();
    }

    public OperatingRoom toEntity() {
        return OperatingRoom.builder()
                .roomCode(roomCode)
                .roomName(roomName)
                .roomType(roomType)
                .status(status != null ? status : RoomStatus.AVAILABLE)
                .active(true)
                .build();
    }
}
