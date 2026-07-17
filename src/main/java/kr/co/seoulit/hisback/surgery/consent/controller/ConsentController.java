package kr.co.seoulit.hisback.surgery.consent.controller;

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
@RequestMapping("/api/v1/surgery")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @GetMapping("/{surgeryId}/consents")
    public ResponseEntity<ApiResponse<List<ConsentDto>>> getConsents(@PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(consentService.getConsents(surgeryId)));
    }

    @PostMapping("/{surgeryId}/consents")
    public ResponseEntity<ApiResponse<ConsentDto>> createConsent(
            @PathVariable String surgeryId, @RequestBody ConsentDto request) {
        request.setSurgeryId(surgeryId);
        ConsentDto created = consentService.createConsent(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }
}
