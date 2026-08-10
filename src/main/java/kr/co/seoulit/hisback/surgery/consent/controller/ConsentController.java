package kr.co.seoulit.hisback.surgery.consent.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.seoulit.hisback.surgery.consent.dto.ConsentDto;
import kr.co.seoulit.hisback.surgery.consent.service.ConsentService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 동의서 관리 컨트롤러 (SL2-53 동의서 확인 / SL2-54 목록조회)
 */
@RestController
@RequestMapping("/api/surgery")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @GetMapping("/{surgeryId}/consents")
    public ResponseEntity<ApiResponse<List<ConsentDto>>> getConsents(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(consentService.getConsents(surgeryId)));
    }

    /**
     * SL2-222: 환자별 동의서 이력 조회
     *
     * <p>경로가 {@code /consents} 라 단건 조회({@code /consents/{consentId}})와 겹치지 않는다.
     * 스프링이 리터럴 경로를 경로변수보다 우선 매칭하므로 선언 순서와 무관하다.</p>
     */
    @GetMapping("/consents")
    public ResponseEntity<ApiResponse<List<ConsentDto>>> getConsentsByPatient(
            @RequestParam String patientId) {
        return ResponseEntity.ok(ApiResponse.success(consentService.getConsentsByPatient(patientId)));
    }

    /** 동의서 단건 조회 */
    @GetMapping("/consents/{consentId}")
    public ResponseEntity<ApiResponse<ConsentDto>> getConsent(@PathVariable String consentId) {
        return ResponseEntity.ok(ApiResponse.success(consentService.getConsent(consentId)));
    }

    /** 동의서 등록 */
    @PostMapping("/{surgeryId}/consents")
    public ResponseEntity<ApiResponse<ConsentDto>> createConsent(
            @PathVariable String surgeryId, @Valid @RequestBody ConsentDto request) {
        request.setSurgeryId(surgeryId);
        ConsentDto created = consentService.createConsent(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }
}
