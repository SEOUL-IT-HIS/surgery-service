package kr.co.seoulit.hisback.surgery.surgeryorder.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageableSupport;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.AssignSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.CreateSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.RejectSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.SurgeryOrderDto;
import kr.co.seoulit.hisback.surgery.surgeryorder.service.SurgeryOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술 오더 컨트롤러 (SL2-36 진료 요청 / SL2-44 응급 요청 / SL2-225 목록 / SL2-226 반려 / SL2-15 배정)
 *
 * <p>진료·응급실이 보내는 <b>요청</b>을 받는 창구다. 수술 자체를 다루는 주소는
 * {@code /api/surgery/schedule} 이고, 이쪽은 그 앞단이다.</p>
 *
 * <p><b>경로를 나눈 이유</b> — 예전에는 {@code POST /api/surgery/schedule} 이 요청 접수를
 * 겸했다. 그런데 그 시점에는 수술실도 확정 시각도 없어서 '일정'이라 부를 것이 없었고,
 * 같은 대상을 읽을 때는 {@code /requests} 라고 부르면서 쓸 때만 {@code /schedule} 이라
 * 부르는 모순이 있었다. 요청은 요청대로 부른다.</p>
 */
@RestController
@RequestMapping("/api/surgery/orders")
public class   SurgeryOrderController {

    private final SurgeryOrderService surgeryOrderService;

    public SurgeryOrderController(SurgeryOrderService surgeryOrderService) {
        this.surgeryOrderService = surgeryOrderService;
    }

    /**
     * 오더 목록 (SL2-225)
     *
     * <p>{@code GET /api/surgery/orders?orderStatusCd=00&emergencyYn=Y&patientId=&fromDt=&toDt=}</p>
     *
     * <p>기본 정렬은 응급 우선, 같은 등급이면 희망일이 빠른 순이다 — 배정 담당자가 먼저
     * 처리해야 할 것이 응급이다. {@code emergency_yn} 이 CHAR(1) 이라 내림차순이면
     * 'Y' 가 'N' 보다 앞선다(§14.2).</p>
     *
     * <p>배정 대기만 보려면 {@code orderStatusCd=00} 을 준다. 조건을 안 주면 반려된 것도 나온다.</p>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SurgeryOrderDto>>> getOrders(
            @RequestParam(required = false) String orderStatusCd,
            @RequestParam(required = false) String emergencyYn,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {

        Pageable pageable =
                PageableSupport.of(
                        page,
                        size,
                        sort,
                        Sort.by(Sort.Order.desc("emergencyYn"), Sort.Order.asc("requestedDt")));

        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryOrderService.getOrders(
                                orderStatusCd, emergencyYn, patientId, fromDt, toDt, pageable)));
    }

    /** 오더 단건 조회. 없으면 404 SUR057. */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<SurgeryOrderDto>> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryOrderService.getOrder(orderId)));
    }

    /**
     * 진료 수술 요청 접수 (SL2-36)
     *
     * <p>{@code POST /api/surgery/orders}</p>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SurgeryOrderDto>> createOrder(
            @Valid @RequestBody CreateSurgeryOrderRequest request) {
        SurgeryOrderDto created = surgeryOrderService.createOrder(request, false);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /**
     * 응급 수술 요청 접수 (SL2-44)
     *
     * <p>{@code POST /api/surgery/orders/emergency}</p>
     *
     * <p>본문은 일반 요청과 같다. <b>경로만 다르다</b> — 응급 여부를 요청 본문으로 받으면
     * 일반 요청이 'Y' 를 실어 배정 우선순위를 가로챌 수 있다.</p>
     */
    @PostMapping("/emergency")
    public ResponseEntity<ApiResponse<SurgeryOrderDto>> createEmergencyOrder(
            @Valid @RequestBody CreateSurgeryOrderRequest request) {
        SurgeryOrderDto created = surgeryOrderService.createOrder(request, true);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /**
     * 수술실 배정 → 오더 수락 (SL2-15)
     *
     * <p>{@code PATCH /api/surgery/orders/{orderId}/assign}</p>
     *
     * <p>주소가 {@code /accept} 가 아니라 {@code /assign} 인 이유 — 담당자가 하는 일은
     * 수술실을 정하는 것이고, 수락은 그 결과다. 수술실이 정해지는 순간 SURGERY 가
     * 만들어지고 오더가 수락(01)으로 바뀐다.</p>
     */
    @PatchMapping("/{orderId}/assign")
    public ResponseEntity<ApiResponse<SurgeryOrderDto>> assignOrder(
            @PathVariable String orderId, @Valid @RequestBody AssignSurgeryOrderRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryOrderService.assignOrder(orderId, request)));
    }

    /**
     * 오더 반려 (SL2-226)
     *
     * <p>{@code PATCH /api/surgery/orders/{orderId}/reject}</p>
     *
     * <p><b>본문과 사유가 모두 필수다</b>. 예전에는 사유 코드 그룹이 admin 에
     * 없어 본문 없이도 통과시켰는데, 그룹을 등록했으므로 그 예외가 필요 없어졌다.
     * 사유가 없거나 비어 있으면 {@code @Valid} 가 400 SUR038 로 막는다(§11.5).</p>
     */
    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<ApiResponse<SurgeryOrderDto>> rejectOrder(
            @PathVariable String orderId,
            @Valid @RequestBody RejectSurgeryOrderRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryOrderService.rejectOrder(orderId, request)));
    }
}
