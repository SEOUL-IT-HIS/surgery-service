package kr.co.seoulit.hisback.surgery.room.controller;

import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
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
// ─────────────────────────────────────────────────────────────────────────────
// 이 파일은 컨트롤러 계층의 '대표 예제'다. 어노테이션마다 설명을 달아두었으니
// 다른 컨트롤러는 같은 모양으로 읽으면 된다.
// ─────────────────────────────────────────────────────────────────────────────

// @RestController — @Controller + @ResponseBody 를 합친 것이다. 두 가지를 한다.
//   1) 이 클래스를 Spring 이 관리하도록 등록한다 (없으면 요청이 와도 404)
//   2) 메서드 반환값을 JSON 으로 직렬화해 응답 본문에 담는다
//      (@Controller 만 붙이면 반환 문자열을 '화면 파일 이름'으로 해석한다)
@RestController
// @RequestMapping — 이 컨트롤러의 모든 메서드가 공유하는 경로 앞부분.
//                   아래 메서드의 경로가 여기에 이어붙는다.
//                   예: @GetMapping("/available") → GET /api/surgery/rooms/available
//                   경로 규칙이 바뀌어도 이 한 줄만 고치면 된다.
@RequestMapping("/api/surgery/rooms")
public class OperatingRoomController {

    // final + 생성자 주입. 생성자가 하나뿐이면 @Autowired 를 생략해도 Spring 이 넣어준다.
    // 필드에 @Autowired 를 붙이는 방식보다 이쪽이 낫다 — final 로 못 박을 수 있고,
    // 테스트에서 가짜 객체를 직접 넣기도 쉽다.
    private final OperatingRoomService operatingRoomService;

    public OperatingRoomController(OperatingRoomService operatingRoomService) {
        this.operatingRoomService = operatingRoomService;
    }

    //getRooms는 GET 전체 요청을 받아 페이지 단위로 OperatingRoomService에 전달(위임) 하고,
    //Service에서 받아온 결과를 ApiResponse로 감싸 반환한다 (SL2-100: page/size/sort)
    // @GetMapping — HTTP GET 에 응답한다. 경로를 안 적었으므로 클래스 경로 그대로다.
    //               같은 경로라도 방식(GET/POST)이 다르면 다른 메서드로 취급되므로,
    //               아래 @PostMapping 과 충돌하지 않는다.
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OperatingRoomDto>>> getRooms(
            // @RequestParam — 물음표 뒤 쿼리스트링에서 값을 꺼낸다. 예: ?page=0&size=20
            //   defaultValue : 안 보내면 이 값을 쓴다(자동으로 선택 항목이 된다)
            //   required=false: 없어도 된다. 안 오면 null
            //   타입이 int 라 null 을 담을 수 없다 — 값이 없을 수 있으면 Integer 를 써야 한다.
            // 대상은 그대로고 '보는 방식'만 바꾸는 값이라 경로가 아니라 쿼리에 둔다.
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
    // 경로 중간에 값이 박힌 형태다. GET /api/surgery/rooms/OR01
    @GetMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> getRoom(
            // @PathVariable — 경로의 {roomCode} 자리 값을 꺼낸다.
            //   중괄호 이름과 파라미터 이름이 같으면 자동으로 연결된다. 다만 이 자동 연결은
            //   컴파일 시 -parameters 옵션이 켜져 있어야 동작한다(Spring Boot Gradle 플러그인이
            //   기본으로 켜준다). 확실히 하려면 @PathVariable("roomCode") 처럼 이름을 적는다.
            //   자원을 '지목'하는 값이라 쿼리가 아니라 경로에 둔다 — 빼면 요청이 성립하지 않는다.
            @PathVariable String roomCode) {
        return ResponseEntity.ok(
                ApiResponse.success(operatingRoomService.getOperatingRoom(roomCode))
        );
    }

    //createRoom은 POST 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    // @PostMapping — 새 자원을 만드는 요청. 보낼 값이 여러 개라 본문(JSON)으로 받는다.
    @PostMapping
    public ResponseEntity<ApiResponse<OperatingRoomDto>> createRoom(
            // @RequestBody — 요청 본문의 JSON 을 Jackson 이 읽어 DTO 객체로 만든다.
            //   빈 객체를 만든 뒤 setter 로 채우므로 DTO 에 @NoArgsConstructor 와 setter 가 필요하다.
            //   JSON 키와 DTO 필드명이 같아야 값이 들어간다(다르면 조용히 null).
            //   Content-Type: application/json 헤더가 없으면 415 로 거절된다.
            //   한 메서드에 하나만 쓸 수 있다 — 본문은 한 번만 읽는 스트림이라서.
            @RequestBody OperatingRoomDto request) {
        OperatingRoomDto created = operatingRoomService.createOperatingRoom(request);
        // 새 자원을 만들었으므로 200 이 아니라 201 Created 로 응답한다.
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateRoom은 PUT 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    // @PutMapping — 표현을 '통째로 교체'할 때 쓴다. 여기서는 수술실 이름을 갈아끼운다.
    //               일부만 바꾸는 상태 변경은 아래처럼 @PatchMapping 으로 따로 뺀다.
    @PutMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> updateRoom(
            // 한 메서드에 @PathVariable 과 @RequestBody 를 함께 쓸 수 있다.
            // 대상은 경로에서, 바꿀 내용은 본문에서 온다.
            @PathVariable String roomCode, @RequestBody OperatingRoomDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(operatingRoomService.updateOperatingRoom(roomCode, request)));
    }

    //changeRoomStatus는 PATCH 요청을 받아 OperatingRoomService에 전달(위임) 하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    //SL2-8: 물리 삭제 대신 상태 전이(주로 CLOSED)로 "제거"를 표현한다
    // @PatchMapping — 일부 필드만 바꾼다. 경로 끝의 /status 는 '무엇을 바꾸는지' 나타내는
    //                 명사다. /changeStatus 같은 동사형은 REST 방식이 아니다(§21.8).
    //
    // 참고: 본문을 Map<String,String> 으로 받으면 @Valid 를 붙일 대상이 없고,
    //       request.get("statusCd") 의 키를 잘못 적어도 컴파일러가 못 잡는다(조용히 null).
    //       record 로 전용 요청 타입을 만드는 편이 안전하다 — 마취 쪽은 이미 그렇게 바꿨다.
    @PatchMapping("/{roomCode}/status")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> changeRoomStatus(
            @PathVariable String roomCode, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        operatingRoomService.changeOperatingRoomStatus(
                                roomCode, request.get("statusCd"))));
    }

    // SL2-50: 턴오버 타임 관리 — status_cd 와는 별개 트랙이라 엔드포인트를 나눈다.
    @PatchMapping("/{roomCode}/turnover")
    public ResponseEntity<ApiResponse<OperatingRoomDto>> changeRoomTurnover(
            @PathVariable String roomCode, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        operatingRoomService.changeOperatingRoomTurnover(
                                roomCode, request.get("turnoverCd"))));
    }
}