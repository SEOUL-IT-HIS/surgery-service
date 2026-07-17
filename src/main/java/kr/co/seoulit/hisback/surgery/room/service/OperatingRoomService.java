package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;

import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;

/**
 * 수술실 관리 서비스 인터페이스 (구현체는 OperatingRoomServiceImpl)
 */
public interface OperatingRoomService {
    List<OperatingRoomDto> getOperatingRooms();

    /** 사용 가능(OR_STATUS_CD=01)한 수술실만 조회한다. */
    List<OperatingRoomDto> getAvailableOperatingRooms();

    OperatingRoomDto createOperatingRoom(OperatingRoomDto request);

    OperatingRoomDto updateOperatingRoom(String roomCode, OperatingRoomDto request);

    /** SL2-8: 물리 삭제 대신 상태 전이(주로 CLOSED)로 "제거"를 표현한다. */
    OperatingRoomDto changeOperatingRoomStatus(String roomCode, String statusCd);
}
