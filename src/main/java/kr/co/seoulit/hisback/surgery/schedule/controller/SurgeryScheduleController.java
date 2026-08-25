package kr.co.seoulit.hisback.surgery.schedule.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageableSupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import kr.co.seoulit.hisback.surgery.schedule.dto.AssignmentRequest;
import kr.co.seoulit.hisback.surgery.schedule.dto.CancelSurgeryRequest;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryStatusHistoryDto;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술 스케줄링 컨트롤러
 * (SL2-25 조회 / SL2-37 수정 / SL2-33 취소 /
 *  SL2-13 집도의배정 / SL2-43 마취의배정 / SL2-63 간호사배정 / SL2-170 배정현황 /
 *  SL2-40 금일현황 / SL2-39 진행상태변경)
 * <p>프론트 api.ts의 /schedule 경로와 1:1로 맞췄다. 응답은 §11.3 ApiResponse&lt;T&gt;로 감싼다.</p>
 */
@RestController
@RequestMapping("/api/surgery/schedule")
public class SurgeryScheduleController {

    private final SurgeryScheduleService surgeryScheduleService;

    public SurgeryScheduleController(SurgeryScheduleService surgeryScheduleService) {
        this.surgeryScheduleService = surgeryScheduleService;
    }

    /**
     * 수술 일정 조회 (SL2-25 / SL2-173 입력값 검증)
     *
     * <p>{@code GET /api/surgery/schedule} — 전체<br>
     * {@code GET /api/surgery/schedule?date=2026-08-13} — 해당 일자</p>
     *
     * <p><b>{@code @DateTimeFormat} 을 명시한 이유</b> — 없어도 Spring 이 ISO 문자열을
     * 알아서 변환해 주는 경우가 많지만, 그건 설정에 기대는 동작이다. 어떤 형식을 받는지
     * 시그니처에 적어두면 Swagger 에도 드러나고, 형식이 어긋났을 때 무엇이 잘못됐는지
     * 분명해진다. 모니터링 컨트롤러와도 같은 모양이 된다.</p>
     *
     * <p>형식이 어긋난 값({@code date=어제})은 GlobalExceptionHandler 의
     * MethodArgumentTypeMismatch 처리가 400 SUR038 로 돌려준다.</p>
     *
     * <p>날짜를 <b>필수로 두지 않았다</b> — 전체 일정을 보는 화면이 실제로 있다.
     * 다만 그 경우 전건을 읽으므로, 건수가 늘면 페이징이 필요해진다(SL2-235 와 같은 성격).</p>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getSchedules(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getSchedules(date)));
    }

    //getSchedule은 수술번호를 통해 특정 수술 정보를 조회해 ApiResponse로 반환한다
    @GetMapping("/{surgeryId}")
    public ResponseEntity<ApiResponse<SurgeryDto>> getSchedule(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getSchedule(surgeryId)));
    }

    // SL2-282: 상태변경 이력 조회
    //   GET /api/surgery/schedule/{surgeryId}/history            → 전체(STATUS+PROGRESS)
    //   GET /api/surgery/schedule/{surgeryId}/history?type=STATUS → 큰 상태 전이만
    //
    //   /{surgeryId} 하위에 둔 이유 — 이력은 수술 한 건에 딸린 것이라 그 수술의 주소 아래가 맞다.
    //   이력만 따로 조회할 일(전체 수술의 이력)은 요구사항에 없어 열지 않는다.
    @GetMapping("/{surgeryId}/history")
    public ResponseEntity<ApiResponse<List<SurgeryStatusHistoryDto>>> getStatusHistory(
            @PathVariable String surgeryId,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.getStatusHistory(surgeryId, type)));
    }

    // 배정 대기 목록은 오더로 옮겼다 — GET /api/surgery/orders?orderStatusCd=00
    //   요청은 이제 SURGERY 가 아니라 SURGERY_ORDER 로 들어온다(2026-08-13 결정).

    /**
     * SL2-170: 수술실 배정 현황 조회
     *
     * <p>{@code GET /api/surgery/schedule/assignments}<br>
     * {@code ...?fromDt=2026-08-01&toDt=2026-08-31&roomCode=ORACLE-01&statusCd=01}</p>
     *
     * <p>수술 건 단위로 평평하게 돌려준다. 진료·응급이 자기 요청의 진행 상태를 물어볼 때도
     * 같은 주소를 쓰므로, 우리 화면 편의만 보고 수술실로 묶지 않았다(§21.3 서비스 간 REST).
     * 빈 방까지 보려면 {@code /api/surgery/monitoring/rooms} 를 함께 부른다.</p>
     *
     * <p>기본 정렬은 수술일 오름차순이다 — 배정 현황은 시간 순으로 훑는 화면이다.</p>
     *
     * <p>{@code /requests} 와 경로를 나눈 이유 — 그쪽은 <b>아직 배정 안 된 것</b>만 보는
     * 고정된 목적이고, 이쪽은 상태를 골라 보는 조회다. 한 주소에 몰면 파라미터 조합으로만
     * 목적이 구분되어 읽기 어려워진다.</p>
     */
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<PageResponse<SurgeryDto>>> getAssignments(
            @RequestParam(required = false) String roomCode,
            @RequestParam(required = false) String statusCd,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String surgeonId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {

        Pageable pageable =
                PageableSupport.of(page, size, sort, Sort.by(Sort.Order.asc("surgeryDt")));

        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.getAssignments(
                                roomCode, statusCd, patientId, surgeonId, fromDt, toDt, pageable)));
    }

    // 수술 등록(진료·응급)은 오더로 옮겼다 — POST /api/surgery/orders, /orders/emergency
    //   수술은 오더가 수락(배정)될 때 만들어지므로, 여기서 직접 만드는 경로는 두지 않는다.

    //updateSchedule은 PUT 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    @PutMapping("/{surgeryId}")
    public ResponseEntity<ApiResponse<SurgeryDto>> updateSchedule(
            @PathVariable String surgeryId, @Valid @RequestBody SurgeryDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.updateSchedule(surgeryId, request)));
    }

    /**
     * 수술 취소·반려 (SL2-33 취소 / SL2-227 반려 사유 입력)
     *
     * <p>{@code PATCH /api/surgery/schedule/{surgeryId}/cancel}</p>
     *
     * <p>물리 삭제 대신 상태 전이(04 취소)로 표현한다(§21.6). 요청접수(00) 상태에서의
     * 취소가 업무상 '반려'다 — 별도 엔드포인트를 두지 않는 이유는 저장되는 것이
     * 같기 때문이고, 둘의 구분은 전이 전 상태로 드러난다(이력의 before_cd).</p>
     *
     * <p>본문 없이 호출해도 된다({@code required = false}). 사유 없는 취소가 업무상 존재한다.</p>
     */
    @PatchMapping("/{surgeryId}/cancel")
    public ResponseEntity<ApiResponse<SurgeryDto>> cancelSchedule(
            @PathVariable String surgeryId,
            @RequestBody(required = false) CancelSurgeryRequest request) {
        String reasonCd = (request != null) ? request.getCancelReasonCd() : null;
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.cancelSchedule(surgeryId, reasonCd)));
    }

    // SL2-15 일괄 배정은 오더로 옮겼다 — PATCH /api/surgery/orders/{orderId}/assign

    // 수술 시작 — 예약→진행중 전이 + 실제 시작일 기록. 완료(/end)와 한 쌍이다.
    @PatchMapping("/{surgeryId}/start")
    public ResponseEntity<ApiResponse<SurgeryDto>> startSurgery(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.startSurgery(surgeryId)));
    }

    /**
     * SL2-13: 집도의 배정
     *
     * <p>집도의는 비울 수 없다 — 수술에 집도의가 없는 상태는 업무상 성립하지 않는다.
     * 나머지 셋은 값을 비워 보내면 배정이 해제된다(SL2-166).</p>
     */
    @PatchMapping("/{surgeryId}/surgeon")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignSurgeon(
            @PathVariable String surgeryId, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignSurgeon(surgeryId, request.getSurgeonId())));
    }

    /**
     * SL2-15 수술실 배정 / SL2-166 변경·해제 / SL2-169 조건 검증
     *
     * <p>{@code roomCode} 를 비워 보내면 배정 해제다. 값이 있으면 수술실이 실재하고
     * 사용가능(01) 상태인지 검증한다 — 없으면 404 SUR036, 점검중·폐쇄면 400 SUR045.</p>
     */
    @PatchMapping("/{surgeryId}/room")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignRoom(
            @PathVariable String surgeryId, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignRoom(surgeryId, request.getRoomCode())));
    }

    /** SL2-43: 마취의 배정. 비워 보내면 해제된다(SL2-166). */
    @PatchMapping("/{surgeryId}/anesthesiologist")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignAnesthesiologist(
            @PathVariable String surgeryId, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignAnesthesiologist(
                                surgeryId, request.getAnesthesiologistId())));
    }

    /** SL2-63: 간호사 배정. 비워 보내면 해제된다(SL2-166). */
    @PatchMapping("/{surgeryId}/nurse")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignNurse(
            @PathVariable String surgeryId, @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignNurse(surgeryId, request.getNurseId())));
    }

    //getTodaySchedules는 GET 요청을 받아 금일 수술 목록을 조회해 ApiResponse로 받아 전달한다
    // SL2-40: 금일 수술현황 대시보드
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getTodaySchedules() {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getTodaySchedules()));
    }

    //updateProgress는 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-39: 당일 실시간 진행상태 변경 (status_cd와 별도 트랙)
    @PatchMapping("/{surgeryId}/progress")
    public ResponseEntity<ApiResponse<SurgeryDto>> updateProgress(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.updateProgress(surgeryId, request.get("progressCd"))));
    }

    // 수술 종료 — status_cd를 03(완료)으로 전이한다. 진행상태(progress_cd)와는 별도 트랙이다.
    // 프론트 계약이 /end 라 이쪽을 정식 경로로 둔다. 시작(/start)과 대칭이다.
    // SL2-72 수납(Billing) 청구 연계는 아직 붙어 있지 않다 — BillingServiceClient 로 REST 호출 예정(§21.3).
    @PatchMapping("/{surgeryId}/end")
    public ResponseEntity<ApiResponse<SurgeryDto>> endSurgery(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.completeSurgery(surgeryId)));
    }

    // 기존 호출부 호환용 별칭. 신규 개발은 /end 를 쓴다.
    @PatchMapping("/{surgeryId}/complete")
    public ResponseEntity<ApiResponse<SurgeryDto>> completeSurgery(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.completeSurgery(surgeryId)));
    }
}
