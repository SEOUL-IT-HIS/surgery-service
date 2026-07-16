package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import org.springframework.stereotype.Service;

/**
 * 수술실 관리 서비스 구현체
 * <p>이름은 XxxServiceImpl, 실제 로직은 여기(class)에 두고 XxxService는 interface로 둔다
 * (기존 코드는 이 둘의 역할이 뒤바뀌어 있었음 — Service가 class, ServiceImpl이 interface).</p>
 */
@Service
public class OperatingRoomServiceImpl implements OperatingRoomService {

    private final OperatingRoomRepository operatingRoomRepository;

    public OperatingRoomServiceImpl(OperatingRoomRepository operatingRoomRepository) {
        this.operatingRoomRepository = operatingRoomRepository;
    }

    @Override
    public List<OperatingRoomDto> getOperatingRooms() {
        return operatingRoomRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OperatingRoomDto createOperatingRoom(OperatingRoomDto request) {
        OperatingRoom room = OperatingRoom.builder()
                .roomCode(request.getRoomCode())
                .roomName(request.getRoomName())
                .build();
        return toDto(operatingRoomRepository.save(room));
    }

    @Override
    public OperatingRoomDto updateOperatingRoom(String roomCode, OperatingRoomDto request) {
        OperatingRoom room = findRoomOrThrow(roomCode);
        room.setRoomName(request.getRoomName());
        return toDto(operatingRoomRepository.save(room));
    }

    @Override
    public OperatingRoomDto changeOperatingRoomStatus(String roomCode, String statusCd) {
        OperatingRoom room = findRoomOrThrow(roomCode);
        room.setStatusCd(statusCd);
        return toDto(operatingRoomRepository.save(room));
    }

    private OperatingRoom findRoomOrThrow(String roomCode) {
        return operatingRoomRepository.findById(roomCode)
                .orElseThrow(() -> new NoSuchElementException("수술실을 찾을 수 없습니다: " + roomCode));
    }

    private OperatingRoomDto toDto(OperatingRoom room) {
        return new OperatingRoomDto(
                room.getRoomCode(),
                room.getRoomName(),
                room.getStatusCd(),
                room.getTurnoverCd(),
                room.getCreatedAt(),
                room.getUpdatedAt());
    }
}
