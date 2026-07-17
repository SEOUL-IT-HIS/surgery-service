package kr.co.seoulit.hisback.surgery.schedule.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
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
@RequestMapping("/api/v1/surgery/schedule")
public class SurgeryScheduleController {

    private final SurgeryScheduleService surgeryScheduleService;

    public SurgeryScheduleController(SurgeryScheduleService surgeryScheduleService) {
        this.surgeryScheduleService = surgeryScheduleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getSchedules(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getSchedules(date)));
    }

    @GetMapping("/{surgeryId}")
    public ResponseEntity<ApiResponse<SurgeryDto>> getSchedule(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getSchedule(surgeryId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SurgeryDto>> registerSchedule(@RequestBody SurgeryDto request) {
        SurgeryDto created = surgeryScheduleService.registerSchedule(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /** SL2-44: 응급 수술은 일정 충돌 검사 없이 우선 배정된다. */
    @PostMapping("/emergency")
    public ResponseEntity<ApiResponse<SurgeryDto>> registerEmergencySchedule(
            @RequestBody SurgeryDto request) {
        SurgeryDto created = surgeryScheduleService.registerEmergencySchedule(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    @PutMapping("/{surgeryId}")
    public ResponseEntity<ApiResponse<SurgeryDto>> updateSchedule(
            @PathVariable String surgeryId, @RequestBody SurgeryDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.updateSchedule(surgeryId, request)));
    }

    /** SL2-33: 물리 삭제 대신 상태 전이(취소)로 표현한다. */
    @PatchMapping("/{surgeryId}/cancel")
    public ResponseEntity<ApiResponse<SurgeryDto>> cancelSchedule(
            @PathVariable String surgeryId, @RequestBody(required = false) Map<String, String> request) {
        String reasonCd = request != null ? request.get("cancelReasonCd") : null;
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.cancelSchedule(surgeryId, reasonCd)));
    }

    /** SL2-13: 집도의 배정 */
    @PatchMapping("/{surgeryId}/surgeon")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignSurgeon(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignSurgeon(surgeryId, request.get("surgeonId"))));
    }

    /** SL2-15: 수술실 배정 */
    @PatchMapping("/{surgeryId}/room")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignRoom(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignRoom(surgeryId, request.get("roomCode"))));
    }

    /** SL2-43: 마취의 배정 */
    @PatchMapping("/{surgeryId}/anesthesiologist")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignAnesthesiologist(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryScheduleService.assignAnesthesiologist(
                                surgeryId, request.get("anesthesiologistId"))));
    }

    /** SL2-63: 간호사 배정 */
    @PatchMapping("/{surgeryId}/nurse")
    public ResponseEntity<ApiResponse<SurgeryDto>> assignNurse(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.assignNurse(surgeryId, request.get("nurseId"))));
    }

    /** SL2-40: 금일 수술현황 대시보드 */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SurgeryDto>>> getTodaySchedules() {
        return ResponseEntity.ok(ApiResponse.success(surgeryScheduleService.getTodaySchedules()));
    }

    /** SL2-39: 당일 실시간 진행상태 변경 (status_cd와 별도 트랙) */
    @PatchMapping("/{surgeryId}/progress")
    public ResponseEntity<ApiResponse<SurgeryDto>> updateProgress(
            @PathVariable String surgeryId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryScheduleService.updateProgress(surgeryId, request.get("progressCd"))));
    }
}
