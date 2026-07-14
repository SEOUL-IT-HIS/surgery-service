package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술실 관리 서비스 로직 (SL2-6/7/8/30)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperatingRoomService {

    private final OperatingRoomRepository operatingRoomRepository;

    /** SL2-6: 수술실 목록 조회 */
    public List<OperatingRoomDto.Response> getRooms() {
        return operatingRoomRepository.findAll().stream()
                .map(OperatingRoomDto.Response::from)
                .toList();
    }

    /** SL2-7: 수술실 추가 */
    @Transactional
    public OperatingRoomDto.Response createRoom(OperatingRoomDto.CreateRequest request) {
        if (operatingRoomRepository.existsById(request.roomCode())) {
            throw BusinessException.conflict("이미 존재하는 수술실 코드입니다: " + request.roomCode());
        }
        OperatingRoom room = OperatingRoom.create(request.roomCode(), request.roomName());
        return OperatingRoomDto.Response.from(operatingRoomRepository.save(room));
    }

    /** SL2-30: 수술실 정보 수정 */
    @Transactional
    public OperatingRoomDto.Response updateRoom(String roomCode, OperatingRoomDto.UpdateRequest request) {
        OperatingRoom room = getRoomOrThrow(roomCode);
        room.updateInfo(request.roomName());
        return OperatingRoomDto.Response.from(room);
    }

    /** SL2-8: 수술실 제거 (물리 삭제 대신 상태 전이) */
    @Transactional
    public OperatingRoomDto.Response changeStatus(String roomCode, OperatingRoomDto.StatusUpdateRequest request) {
        OperatingRoom room = getRoomOrThrow(roomCode);
        room.changeStatus(request.statusCd());
        return OperatingRoomDto.Response.from(room);
    }

    private OperatingRoom getRoomOrThrow(String roomCode) {
        return operatingRoomRepository.findById(roomCode)
                .orElseThrow(() -> BusinessException.notFound("수술실을 찾을 수 없습니다: " + roomCode));
    }
}
