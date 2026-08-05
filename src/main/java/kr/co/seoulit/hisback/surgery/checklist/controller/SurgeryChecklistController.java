package kr.co.seoulit.hisback.surgery.checklist.controller;

import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.service.SurgeryChecklistService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술안전체크리스트 컨트롤러 (SL2-46 SignIn / SL2-47 TimeOut / SL2-48 SignOut / SL2-49 사후수정 / SL2-35 조회)
 */
@RestController
@RequestMapping("/api/surgery")
public class SurgeryChecklistController {

    private final SurgeryChecklistService surgeryChecklistService;

    public SurgeryChecklistController(SurgeryChecklistService surgeryChecklistService) {
        this.surgeryChecklistService = surgeryChecklistService;
    }

    @GetMapping("/{surgeryId}/checklist")
    public ResponseEntity<ApiResponse<List<SurgeryChecklistDto>>> getChecklist(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryChecklistService.getChecklist(surgeryId)));
    }

    @PostMapping("/{surgeryId}/checklist")
    public ResponseEntity<ApiResponse<SurgeryChecklistDto>> createChecklistItem(
            @PathVariable String surgeryId, @RequestBody SurgeryChecklistDto request) {
        request.setSurgeryId(surgeryId);
        SurgeryChecklistDto created = surgeryChecklistService.createChecklistItem(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    @PatchMapping("/checklist/{checklistId}")
    public ResponseEntity<ApiResponse<SurgeryChecklistDto>> updateChecklistItem(
            @PathVariable String checklistId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryChecklistService.updateChecklistItem(checklistId, request.get("completedYn"))));
    }
}
