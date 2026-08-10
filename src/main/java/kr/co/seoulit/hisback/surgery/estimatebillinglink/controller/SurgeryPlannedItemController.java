package kr.co.seoulit.hisback.surgery.estimatebillinglink.controller;

import kr.co.seoulit.hisback.surgery.estimatebillinglink.service.SurgeryPlannedItemService;

/**
 * 수술 예정 자원목록 컨트롤러 (SL2-65 등록 / SL2-66 조회)
 *
 * <p><b>보류 중이다.</b> {@code @RestController} 를 붙이지 않아 Spring 이 등록하지 않는다.
 * 따라서 Swagger 문서에도 나오지 않는다 — 아직 계약이 확정되지 않은 API 를
 * 문서에 노출하면 다른 팀이 그걸 보고 붙는다.</p>
 *
 * <p>SL2-65·66 은 Jira 설명이 비어 있어 요구사항이 확정되지 않았다.
 * 스프린트 7 · 우선순위 Lowest.</p>
 */
public class SurgeryPlannedItemController {

    private final SurgeryPlannedItemService surgeryPlannedItemService;

    public SurgeryPlannedItemController(SurgeryPlannedItemService surgeryPlannedItemService) {
        this.surgeryPlannedItemService = surgeryPlannedItemService;
    }

    /* ── 초안 (요구사항 확정 후 @RestController 와 함께 되살린다) ──────────
     *
     * // 클래스에 @RestController @RequestMapping("/api/surgery") 를 붙인다.
     * // 예정 자원은 항상 특정 수술에 속하므로 경로를 중첩하고,
     * // 개별 삭제는 품목 ID 만으로 대상이 정해지므로 평평하게 둔다(§21.8).
     *
     * @GetMapping("/{surgeryId}/planned-items")
     * public ResponseEntity<ApiResponse<List<SurgeryPlannedItemDto>>> getPlannedItems(
     *         @PathVariable String surgeryId) {
     *     return ResponseEntity.ok(
     *             ApiResponse.success(surgeryPlannedItemService.getPlannedItems(surgeryId)));
     * }
     *
     * @PostMapping("/{surgeryId}/planned-items")
     * public ResponseEntity<ApiResponse<SurgeryPlannedItemDto>> createPlannedItem(
     *         @PathVariable String surgeryId,
     *         @Valid @RequestBody SurgeryPlannedItemDto request) {
     *     // 본문의 surgeryId 는 신뢰하지 않고 경로 값으로 덮어쓴다
     *     request.setSurgeryId(surgeryId);
     *     SurgeryPlannedItemDto created = surgeryPlannedItemService.createPlannedItem(request);
     *     return ResponseEntity.status(201).body(ApiResponse.success(201, created));
     * }
     *
     * @DeleteMapping("/planned-items/{plannedItemId}")
     * public ResponseEntity<ApiResponse<Void>> deletePlannedItem(@PathVariable String plannedItemId) {
     *     surgeryPlannedItemService.deletePlannedItem(plannedItemId);
     *     return ResponseEntity.ok(ApiResponse.<Void>success(null));
     * }
     *
     * ────────────────────────────────────────────────────────────── */
}
