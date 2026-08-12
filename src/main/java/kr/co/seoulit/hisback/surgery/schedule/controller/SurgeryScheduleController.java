package kr.co.seoulit.hisback.surgery.schedule.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryStatusHistoryDto;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술 스케줄링 컨트롤러
 * (SL2-25 조회 / SL2-36 등록 / SL2-44 응급등록 / SL2-37 수정 / SL2-33 취소 /
 *  SL2-13 집도의배정 / SL2-15 수술실배정 / SL2-43 마취의배정 / SL2-63 간호사배정 /
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

    //getSchedules는 GET 요청을 받아 SurgeryScheduleService에 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getSchedules(
            @RequestParam(required = false) LocalDate date) {
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

    // SL2-225: 배정 대기 목록 — 진료·응급실이 요청했으나 아직 수술실이 잡히지 않은 건(응급 우선)
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getRequestedSchedules() {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getRequestedSchedules()));
    }

    //registerSchedule은 POST 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ResponseEntity로 받아 전달한다
    @PostMapping
    public ResponseEntity<ApiResponse<SurgeryDto>> registerSchedule(@Valid @RequestBody SurgeryDto request) {
        SurgeryDto created = surgeryScheduleService.registerSchedule(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //registerEmergencySchedule은 POST 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-44: 응급 수술은 일정 충돌 검사 없이 우선 배정된다
    @PostMapping("/emergency")
    public ResponseEntity<ApiResponse<SurgeryDto>> registerEmergencySchedule(
            @Valid @RequestBody SurgeryDto request) {
        SurgeryDto created = surgeryScheduleService.registerEmergencySchedule(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateSchedule은 PUT 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    @PutMapping("/{surgeryId}")
    public ResponseEntity<ApiResponse<SurgeryDto>> updateSchedule(
            @PathVariable String surgeryId, @Valid @RequestBody SurgeryDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.updateSchedule(surgeryId, request)));
    }

    //cancelSchedule은 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-33: 물리 삭제 대신 상태 전이(취소)로 표현한다
    @PatchMapping("/{surgeryId}/cancel")
    public ResponseEntity<ApiResponse<SurgeryDto>> cancelSchedule(
            @PathVariable String surgeryId, @RequestBody(required = false) Map<String, String> request) {
        String reasonCd = request != null ? request.get("cancelReasonCd") : null;
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.cancelSchedule(surgeryId, reasonCd)));
    }

    // SL2-15: 수술 배정 — 수술실·마취의·간호사를 한 번에 채우고 요청접수→예약으로 전이한다.
    // 아래 개별 배정 API(/surgeon, /room, ...)는 배정 후 부분 변경용으로 남긴다.
    @PatchMapping("/{surgeryId}/assign")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignSurgery(
            @PathVariable String surgeryId, @Valid @RequestBody SurgeryDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignSurgery(surgeryId, request)));
    }

    // 수술 시작 — 예약→진행중 전이 + 실제 시작일 기록. 완료(/end)와 한 쌍이다.
    @PatchMapping("/{surgeryId}/start")
    public ResponseEntity<ApiResponse<SurgeryDto>> startSurgery(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.startSurgery(surgeryId)));
    }

    //assignSurgeon은 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-13: 집도의 배정
    @PatchMapping("/{surgeryId}/surgeon")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignSurgeon(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignSurgeon(surgeryId, request.get("surgeonId"))));
    }

    //assignRoom은 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-15: 수술실 배정
    @PatchMapping("/{surgeryId}/room")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignRoom(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignRoom(surgeryId, request.get("roomCode"))));
    }

    //assignAnesthesiologist는 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-43: 마취의 배정
    @PatchMapping("/{surgeryId}/anesthesiologist")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignAnesthesiologist(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignAnesthesiologist(
                                surgeryId, request.get("anesthesiologistId"))));
    }

    //assignNurse는 PATCH 요청을 받아 SurgeryScheduleService로 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 받아 전달한다
    // SL2-63: 간호사 배정
    @PatchMapping("/{surgeryId}/nurse")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignNurse(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignNurse(surgeryId, request.get("nurseId"))));
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
