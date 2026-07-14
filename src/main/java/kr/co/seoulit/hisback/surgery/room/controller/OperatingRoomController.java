package kr.co.seoulit.hisback.surgery.room.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.service.OperatingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술실 관리 컨트롤러 (SL2-6 조회 / SL2-7 추가 / SL2-8 제거 / SL2-30 정보수정)
 */
@RestController
@RequestMapping("/api/v1/surgery/rooms")
@RequiredArgsConstructor
public class OperatingRoomController {

    private final OperatingRoomService operatingRoomService;

    /** SL2-6: 수술실 목록 조회 */
    @GetMapping
    public ApiResponse<List<OperatingRoomDto.Response>> getRooms() {
        return ApiResponse.ok(operatingRoomService.getRooms());
    }

    /** SL2-7: 수술실 추가 */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<OperatingRoomDto.Response> createRoom(@Valid @RequestBody OperatingRoomDto.CreateRequest request) {
        return ApiResponse.ok(operatingRoomService.createRoom(request));
    }

    /** SL2-30: 수술실 정보 수정 */
    @PutMapping("/{roomCode}")
    public ApiResponse<OperatingRoomDto.Response> updateRoom(
            @PathVariable String roomCode,
            @Valid @RequestBody OperatingRoomDto.UpdateRequest request) {
        return ApiResponse.ok(operatingRoomService.updateRoom(roomCode, request));
    }

    /**
     * SL2-8: 수술실 제거
     * <p>물리 DELETE 대신 상태 전이(PATCH .../status)로 처리한다.
     * (개발표준가이드 §14 취지: 이력 보존을 위해 물리 삭제 지양)</p>
     */
    @PatchMapping("/{roomCode}/status")
    public ApiResponse<OperatingRoomDto.Response> changeStatus(
            @PathVariable String roomCode,
            @Valid @RequestBody OperatingRoomDto.StatusUpdateRequest request) {
        return ApiResponse.ok(operatingRoomService.changeStatus(roomCode, request));
    }
}
