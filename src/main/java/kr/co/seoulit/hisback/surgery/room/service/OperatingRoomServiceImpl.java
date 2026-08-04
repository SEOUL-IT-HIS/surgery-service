package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 수술실 관리 서비스 구현체
 * <p>이름은 XxxServiceImpl, 실제 로직은 여기(class)에 두고 XxxService는 interface로 둔다
 * (기존 코드는 이 둘의 역할이 뒤바뀌어 있었음 — Service가 class, ServiceImpl이 interface).</p>
 */
@Service
public class OperatingRoomServiceImpl implements OperatingRoomService {

    /** OR_STATUS_CD 01=사용가능 (02사용중/03점검중/04폐쇄) */
    private static final String STATUS_AVAILABLE = "01";

    private final OperatingRoomRepository operatingRoomRepository;

    public OperatingRoomServiceImpl(OperatingRoomRepository operatingRoomRepository) {
        this.operatingRoomRepository = operatingRoomRepository;
    }

    // SL2-100: page/size/sort는 컨트롤러가 Pageable로 조립해 넘긴다. Repository는 JpaRepository를
    // 상속하고 있어 findAll(Pageable)이 기본 제공되므로 별도 쿼리 메서드 없이 그대로 쓴다.
    @Override
    public PageResponse<OperatingRoomDto> getOperatingRooms(Pageable pageable) {
        Page<OperatingRoom> result = operatingRoomRepository.findAll(pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    // SL2-76: 수술실 코드로 단건 상세 조회
    @Override
    public OperatingRoomDto getOperatingRoomfindById(String roomCode) {
        return toDto(findRoomOrThrow(roomCode));
    }

    @Override
    public List<OperatingRoomDto> getAvailableOperatingRooms() {
        return operatingRoomRepository.findByStatusCd(STATUS_AVAILABLE).stream()
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

    /**
     * SL2-50: 턴오버 상태 변경
     *
     * <p>빈 값을 막는 이유 — 프론트 셀렉트의 '미지정' 옵션이 빈 문자열을 보내면 턴오버가
     * 지워져 정리 진행 상황을 잃는다. 화면에서도 막지만 API 직접 호출까지 고려해 여기서도 검증한다.</p>
     */
    @Override
    public OperatingRoomDto changeOperatingRoomTurnover(String roomCode, String turnoverCd) {
        if (turnoverCd == null || turnoverCd.isBlank()) {
            throw new IllegalArgumentException("턴오버 코드는 필수입니다");
        }
        OperatingRoom room = findRoomOrThrow(roomCode);
        room.setTurnoverCd(turnoverCd);
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
