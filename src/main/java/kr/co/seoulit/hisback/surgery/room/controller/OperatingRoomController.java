package kr.co.seoulit.hisback.surgery.room.controller;

import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.OperatingRoomDto;
import kr.co.seoulit.hisback.surgery.room.service.OperatingRoomService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술실 관리 컨트롤러 (SL2-6 조회 / SL2-7 추가 / SL2-30 정보수정 / SL2-8 상태변경)
 * <p>프론트 api.ts(hisfrontend/src/features/surgery/api.ts)의 getRooms/createRoom/
 * updateRoom/changeRoomStatus 호출 경로와 1:1로 맞췄다. 응답은 가이드 §11.3에 따라
 * ApiResponse<T>(code/message/data)로 감싸고, 예외는 GlobalExceptionHandler가
 * 공통으로 code/message를 채워 처리한다(§11.5) — 여기서는 try/catch를 직접 하지 않는다.</p>
 */
@RestController
@RequestMapping("/api/v1/surgery/rooms")
public class OperatingRoomController {

    private final OperatingRoomService operatingRoomService;

    public OperatingRoomController(OperatingRoomService operatingRoomService) {
        this.operatingRoomService = operatingRoomService;
    }

    //getRooms는 GET 전체 요청을 받아 페이지 단위로 OperatingRoomService에 전달(위임) 하고,
    //Service에서 받아온 결과를 ApiResponse로 감싸 반환한다 (SL2-100: page/size/sort)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OperatingRoomDto>>> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        Pageable pageable = (sort != null && !sort.isBlank())
                ? PageRequest.of(page, size, Sort.by(sort))
                : PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(operatingRoomService.getOperatingRooms(pageable)));
    }

    //getAvailableRooms는 사용 가능(status_cd=01)한 수술실만 조회해 ApiResponse로 감싸 반환한다
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<OperatingRoomDto>>> getAvailableRooms() {
        return ResponseEntity.ok(
                ApiResponse.success(operatingRoomService.getAvailableOperatingRooms()));
    }

    //getRoom은 수술실 코드로 특정 수술실을 조회해 ApiResponse로 반환한다
    @GetMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> getRoom(@PathVariable String roomCode) {
        return ResponseEntity.ok(
                ApiResponse.success(operatingRoomService.getOperatingRoomfindById(roomCode))
        );
    }

    //createRoom은 POST 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    @PostMapping
    public ResponseEntity<ApiResponse<OperatingRoomDto>> createRoom(
            @RequestBody OperatingRoomDto request) {
        OperatingRoomDto created = operatingRoomService.createOperatingRoom(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateRoom은 PUT 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    @PutMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> updateRoom(
            @PathVariable String roomCode, @RequestBody OperatingRoomDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(operatingRoomService.updateOperatingRoom(roomCode, request)));
    }

    //changeRoomStatus는 PATCH 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    //SL2-8: 물리 삭제 대신 상태 전이(주로 CLOSED)로 "제거"를 표현한다
    @PatchMapping("/{roomCode}/status")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> changeRoomStatus(
            @PathVariable String roomCode, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        operatingRoomService.changeOperatingRoomStatus(
                                roomCode, request.get("statusCd"))));
    }


}