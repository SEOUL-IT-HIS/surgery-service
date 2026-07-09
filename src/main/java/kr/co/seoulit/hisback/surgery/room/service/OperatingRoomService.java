package kr.co.seoulit.hisback.surgery.room.service;

import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.entity.RoomStatus;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 수술실 관리 서비스 로직 (FR-SUR-001)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OperatingRoomService {

    private final OperatingRoomRepository operatingRoomRepository;

    /** 수술실 목록 조회 (SL2-6) */
    @Transactional(readOnly = true)
    public List<OperatingRoomDto> getRooms() {
        return operatingRoomRepository.findByActiveTrue()
                .stream().map(OperatingRoomDto::from).toList();
    }

    /** 수술실 추가 (SL2-7) */
    public OperatingRoomDto addRoom(OperatingRoomDto dto) {
        if (operatingRoomRepository.existsByRoomCode(dto.getRoomCode())) {
            throw new BusinessException("이미 존재하는 수술방 코드입니다: " + dto.getRoomCode());
        }
        return OperatingRoomDto.from(operatingRoomRepository.save(dto.toEntity()));
    }

    /** 수술실 정보 수정 (SL2-30) */
    public OperatingRoomDto updateRoom(Long id, OperatingRoomDto dto) {
        OperatingRoom room = findOrThrow(id);
        room.setRoomName(dto.getRoomName());
        room.setRoomType(dto.getRoomType());
        if (dto.getStatus() != null) {
            room.setStatus(dto.getStatus());
        }
        return OperatingRoomDto.from(room);
    }

    /** 수술실 제거 (SL2-8) — 사용 중이면 거부하고, 그 외에는 소프트 삭제한다. */
    public void removeRoom(Long id) {
        OperatingRoom room = findOrThrow(id);
        if (room.getStatus() == RoomStatus.IN_USE) {
            throw new BusinessException("사용 중인 수술실은 제거할 수 없습니다: " + room.getRoomCode());
        }
        room.setActive(false);
        room.setStatus(RoomStatus.UNAVAILABLE);
    }

    private OperatingRoom findOrThrow(Long id) {
        return operatingRoomRepository.findById(id)
                .orElseThrow(() -> new BusinessException("수술실 정보를 찾을 수 없습니다. id=" + id));
    }
}
