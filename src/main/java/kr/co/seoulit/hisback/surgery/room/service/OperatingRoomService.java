package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;

import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import org.springframework.data.domain.Pageable;

/**
 * 수술실 관리 서비스 인터페이스 (구현체는 OperatingRoomServiceImpl)
 */
public interface OperatingRoomService {
    /** SL2-6/SL2-100: 목록을 페이지 단위로 조회한다. */
    PageResponse<OperatingRoomDto> getOperatingRooms(Pageable pageable);

    /** 사용 가능(OR_STATUS_CD=01)한 수술실만 조회한다. */
    List<OperatingRoomDto> getAvailableOperatingRooms();

    OperatingRoomDto createOperatingRoom(OperatingRoomDto request);

    OperatingRoomDto updateOperatingRoom(String roomCode, OperatingRoomDto request);

    /** SL2-8: 물리 삭제 대신 상태 전이(주로 CLOSED)로 "제거"를 표현한다. */
    OperatingRoomDto changeOperatingRoomStatus(String roomCode, String statusCd);

    /**
     * SL2-50: 턴오버 상태 변경 (TURNOVER_CD: 01정리중/02준비완료)
     *
     * <p>status_cd(사용가능·점검중·폐쇄)와는 별개 트랙이다. 수술 사이 정리 진행 상황만 나타내므로
     * 점검중·폐쇄 수술실에는 의미가 없어 호출부에서 막는다.</p>
     */
    OperatingRoomDto changeOperatingRoomTurnover(String roomCode, String turnoverCd);

    OperatingRoomDto getOperatingRoomfindById(String roomCode);
}
