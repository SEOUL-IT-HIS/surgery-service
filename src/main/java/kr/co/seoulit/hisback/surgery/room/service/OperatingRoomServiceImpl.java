package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 수술실 관리 서비스 구현체
 * <p>이름은 XxxServiceImpl, 실제 로직은 여기(class)에 두고 XxxService는 interface로 둔다</p>
 */
// ─────────────────────────────────────────────────────────────────────────────
// 이 파일은 서비스 계층의 '대표 예제'다. 다른 ServiceImpl 도 같은 모양이다.
//
// 인터페이스(OperatingRoomService)와 구현체(OperatingRoomServiceImpl)를 나눈 이유 —
// 컨트롤러는 인터페이스만 알면 되므로, 구현을 통째로 바꿔도 컨트롤러는 그대로다.
// 테스트에서 가짜 구현을 끼워 넣기도 쉽다.
// ─────────────────────────────────────────────────────────────────────────────

// @Service — 이 클래스를 Spring 이 관리하는 빈으로 등록한다.
//   기능만 보면 @Component 와 같지만, 이름으로 "여기가 업무 규칙을 담는 곳"임을 드러낸다.
//   이게 없으면 컨트롤러가 주입받을 대상을 못 찾아 기동 시점에 실패한다.
//
// @Transactional 이 없는 점에 주목할 것 —
//   지금은 메서드마다 save() 를 한 번씩만 호출해서, Spring Data 가 각 save 를
//   자체 트랜잭션으로 처리해 문제없이 동작한다. 하지만 "취소하면서 배정도 푼다"처럼
//   두 가지를 한꺼번에 바꾸는 순간 깨진다 — 중간에 실패하면 앞의 변경만 남는다.
//   그런 메서드를 만들 때는 @Transactional 을 반드시 붙여야 한다.
@Service
public class OperatingRoomServiceImpl implements OperatingRoomService {

    /**
     * OR_STATUS_CD 01=사용가능 (02사용중/03점검중/04폐쇄)
     *
     * <p>문자열을 코드 곳곳에 흩어놓지 않고 상수 하나로 모은다. "01" 을 직접 적으면
     * 오타가 나도 컴파일러가 못 잡고, 값이 바뀔 때 찾을 곳이 늘어난다.
     * 코드 카탈로그 자체는 admin-service 소유라 여기서는 값만 들고 쓴다(§21.4).</p>
     */
    private static final String STATUS_AVAILABLE = "01";

    private final OperatingRoomRepository operatingRoomRepository;

    // 생성자 주입. 생성자가 하나뿐이라 @Autowired 를 생략해도 Spring 이 넣어준다.
    // new OperatingRoomRepository() 를 직접 하지 않으므로, 구현이 바뀌어도 이 클래스는 그대로다.
    public OperatingRoomServiceImpl(OperatingRoomRepository operatingRoomRepository) {
        this.operatingRoomRepository = operatingRoomRepository;
    }

    // SL2-100: page/size/sort는 컨트롤러가 Pageable로 조립해 넘긴다. Repository는 JpaRepository를
    // 상속하고 있어 findAll(Pageable)이 기본 제공되므로 별도 쿼리 메서드 없이 그대로 쓴다.
    //
    // @Override — 인터페이스(OperatingRoomService)에 선언된 메서드를 구현한다는 표시.
    //   없어도 동작하지만 붙이는 편이 안전하다. 인터페이스의 메서드명이 바뀌었는데 여기를
    //   안 고치면 컴파일 에러로 잡아주기 때문이다. 없으면 "구현하지 않은 별개 메서드"가
    //   조용히 생기고, 인터페이스 쪽은 미구현 상태로 남는다.
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
    public OperatingRoomDto getOperatingRoom(String roomCode) {
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "turnoverCd 누락");
        }
        OperatingRoom room = findRoomOrThrow(roomCode);
        room.setTurnoverCd(turnoverCd);
        return toDto(operatingRoomRepository.save(room));
    }

    private OperatingRoom findRoomOrThrow(String roomCode) {
        return operatingRoomRepository.findById(roomCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SURGERY_ROOM_NOT_FOUND, roomCode));
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
