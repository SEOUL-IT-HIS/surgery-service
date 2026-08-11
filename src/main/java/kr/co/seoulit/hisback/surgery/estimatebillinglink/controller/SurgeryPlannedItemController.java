package kr.co.seoulit.hisback.surgery.estimatebillinglink.controller;

import jakarta.validation.Valid;
import kr.co.seoulit.hisback.surgery.estimatebillinglink.dto.SurgeryPlannedItemDto;
import kr.co.seoulit.hisback.surgery.estimatebillinglink.service.SurgeryPlannedItemService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 수술 예정 자원목록 컨트롤러 (SL2-65 등록 / SL2-66 조회)
 */
@RestController
@RequestMapping("/api/surgery")
public class SurgeryPlannedItemController {

    private final SurgeryPlannedItemService surgeryPlannedItemService;

    public SurgeryPlannedItemController(SurgeryPlannedItemService surgeryPlannedItemService) {
        this.surgeryPlannedItemService = surgeryPlannedItemService;
    }

    @GetMapping("/{surgeryId}/planned-items")
    public ResponseEntity<ApiResponse<List<SurgeryPlannedItemDto>>> getPlannedItems(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryPlannedItemService.getPlannedItems(surgeryId)));
    }

    @PostMapping("/{surgeryId}/planned-items")
    public ResponseEntity<ApiResponse<SurgeryPlannedItemDto>> createPlannedItem(
            @PathVariable String surgeryId,
            @Valid @RequestBody SurgeryPlannedItemDto request) {
        // 본문의 surgeryId 는 신뢰하지 않고 경로 값으로 덮어쓴다
        request.setSurgeryId(surgeryId);
        SurgeryPlannedItemDto created = surgeryPlannedItemService.createPlannedItem(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    @DeleteMapping("/planned-items/{plannedItemId}")
    public ResponseEntity<ApiResponse<Void>> deletePlannedItem(@PathVariable String plannedItemId) {
        surgeryPlannedItemService.deletePlannedItem(plannedItemId);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

}
