package kr.co.seoulit.hisback.surgery.anesthesia.controller;

import jakarta.validation.Valid;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AppendVitalSignsRequest;
import kr.co.seoulit.hisback.surgery.anesthesia.service.AnesthesiaRecordService;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.common.response.PageableSupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * SL2-34/246: 특정 수술의 마취기록 목록 (페이지 단위)
     *
     * <p>{@code GET /api/surgery/{surgeryId}/anesthesia-records?page=0&size=20&sort=createdAt,desc}</p>
     *
     * <p>page/size/sort 조립을 컨트롤러가 맡는 것은 수술실 목록(SL2-100)과 같은 방식이다.
     * 서비스는 {@code Pageable} 만 받아 무엇으로 정렬되는지 신경 쓰지 않는다.</p>
     *
     * <p><b>정렬을 안 보내면 작성 시각 역순</b>이다. 정렬을 지정하지 않으면 DB가 돌려주는
     * 순서에 맡기게 되는데, 그 순서는 보장되지 않아 페이지를 넘길 때 같은 행이 두 번
     * 나오거나 빠질 수 있다. 기본값을 두어 그 사고를 막는다.</p>
     *
     * <p>응답이 배열에서 PageResponse 로 바뀌었다 — 프론트 api.ts 도 함께 고쳤다.</p>
     */
    @GetMapping("/{surgeryId}/anesthesia-records")
    public ResponseEntity<ApiResponse<PageResponse<AnesthesiaRecordDto>>> getAnesthesiaRecords(
            @PathVariable String surgeryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        Pageable pageable =
                PageableSupport.of(
                        page, size, sort, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(
                ApiResponse.success(
                        anesthesiaRecordService.getAnesthesiaRecords(surgeryId, pageable)));
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
