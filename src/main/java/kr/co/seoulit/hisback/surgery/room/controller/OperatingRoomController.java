package kr.co.seoulit.hisback.surgery.room.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.service.OperatingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술실 관리 컨트롤러 (SL2-6 조회 / SL2-7 추가 / SL2-8 제거 / SL2-30 정보수정)
 */
@RestController
@RequestMapping("/api/v1/surgery/rooms")
@RequiredArgsConstructor
public class OperatingRoomController {

    private final OperatingRoomService operatingRoomService;

    /** 수술실 목록 조회 (SL2-6) */
    @GetMapping
    public ApiResponse<List<OperatingRoomDto>> list() {
        return ApiResponse.ok(operatingRoomService.getRooms());
    }

    /** 수술실 추가 (SL2-7) */
    @PostMapping
    public ApiResponse<OperatingRoomDto> add(@RequestBody OperatingRoomDto dto) {
        return ApiResponse.ok("수술실이 추가되었습니다.", operatingRoomService.addRoom(dto));
    }

    /** 수술실 정보 수정 (SL2-30) */
    @PutMapping("/{id}")
    public ApiResponse<OperatingRoomDto> update(@PathVariable Long id, @RequestBody OperatingRoomDto dto) {
        return ApiResponse.ok("수술실 정보가 수정되었습니다.", operatingRoomService.updateRoom(id, dto));
    }

    /** 수술실 제거 (SL2-8) */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        operatingRoomService.removeRoom(id);
        return ApiResponse.ok("수술실이 제거되었습니다.", null);
    }
}
