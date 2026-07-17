package kr.co.seoulit.hisback.surgery.anesthesia.controller;

import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.service.AnesthesiaRecordService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 마취기록 컨트롤러 (SL2-18 활력징후 기록 / SL2-21 약물투여기록 / SL2-34 조회)
 * <p>프론트 api.ts의 /{surgeryId}/anesthesia-records, /anesthesia-records/{id}/vital-signs
 * 경로와 맞췄다.</p>
 */
@RestController
@RequestMapping("/api/v1/surgery")
public class AnesthesiaRecordController {

    private final AnesthesiaRecordService anesthesiaRecordService;

    public AnesthesiaRecordController(AnesthesiaRecordService anesthesiaRecordService) {
        this.anesthesiaRecordService = anesthesiaRecordService;
    }

    @GetMapping("/{surgeryId}/anesthesia-records")
    public ResponseEntity<ApiResponse<List<AnesthesiaRecordDto>>> getAnesthesiaRecords(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(anesthesiaRecordService.getAnesthesiaRecords(surgeryId)));
    }

    @PostMapping("/{surgeryId}/anesthesia-records")
    public ResponseEntity<ApiResponse<AnesthesiaRecordDto>> createAnesthesiaRecord(
            @PathVariable String surgeryId, @RequestBody AnesthesiaRecordDto request) {
        request.setSurgeryId(surgeryId);
        AnesthesiaRecordDto created = anesthesiaRecordService.createAnesthesiaRecord(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /** SL2-18: 활력징후는 CLOB 로그에 이어붙이는 방식이라 PATCH로 처리한다. */
    @PatchMapping("/anesthesia-records/{anesthesiaId}/vital-signs")
    public ResponseEntity<ApiResponse<AnesthesiaRecordDto>> appendVitalSigns(
            @PathVariable String anesthesiaId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        anesthesiaRecordService.appendVitalSigns(anesthesiaId, request.get("vitalSignsLog"))));
    }
}
