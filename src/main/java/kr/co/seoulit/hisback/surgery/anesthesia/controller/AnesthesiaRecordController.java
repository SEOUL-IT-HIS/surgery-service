package kr.co.seoulit.hisback.surgery.anesthesia.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AppendVitalSignsRequest;
import kr.co.seoulit.hisback.surgery.anesthesia.service.AnesthesiaRecordService;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 마취기록 컨트롤러 (SL2-18 활력징후 기록 / SL2-21 약물투여기록 / SL2-34 조회)
 * <p>프론트 api.ts의 /{surgeryId}/anesthesia-records, /anesthesia-records/{id},
 * /anesthesia-records/{id}/vital-signs 경로와 맞췄다.</p>
 */
@RestController
@RequestMapping("/api/surgery")
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

    /**
     * SL2-247: 마취기록 단건 조회
     *
     * <p>경로가 위의 /{surgeryId}/anesthesia-records 와 헷갈릴 것 같지만 충돌하지 않는다.
     * 두 번째 칸이 고정 문자열(anesthesia-records)이냐 값이냐로 갈리기 때문이다.
     * GET /api/surgery/SUR-1/anesthesia-records  → 위 목록 조회
     * GET /api/surgery/anesthesia-records/AN-1   → 이 메서드
     * Spring 은 변수 자리보다 <b>고정 문자열이 앞선 패턴</b>을 더 구체적인 것으로 보고 고른다.
     * 동의서 컨트롤러의 /consents/{consentId} 도 같은 구조다.</p>
     */
    @GetMapping("/anesthesia-records/{anesthesiaId}")
    public ResponseEntity<ApiResponse<AnesthesiaRecordDto>> getAnesthesiaRecord(
            @PathVariable String anesthesiaId) {
        return ResponseEntity.ok(ApiResponse.success(anesthesiaRecordService.getAnesthesiaRecord(anesthesiaId)));
    }

    @PostMapping("/{surgeryId}/anesthesia-records")
    public ResponseEntity<ApiResponse<AnesthesiaRecordDto>> createAnesthesiaRecord(
            @PathVariable String surgeryId, @Valid @RequestBody AnesthesiaRecordDto request) {
        request.setSurgeryId(surgeryId);
        AnesthesiaRecordDto created = anesthesiaRecordService.createAnesthesiaRecord(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /**
     * SL2-18: 활력징후는 CLOB 로그에 이어붙이는 방식이라 PATCH로 처리한다.
     * <p>SL2-206: 빈 값이 로그에 섞이지 않도록 전용 요청 DTO 로 필수 검증한다.</p>
     */
    @PatchMapping("/anesthesia-records/{anesthesiaId}/vital-signs")
    public ResponseEntity<ApiResponse<AnesthesiaRecordDto>> appendVitalSigns(
            @PathVariable String anesthesiaId, @Valid @RequestBody AppendVitalSignsRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        anesthesiaRecordService.appendVitalSigns(anesthesiaId, request.vitalSignsLog())));
    }
}
