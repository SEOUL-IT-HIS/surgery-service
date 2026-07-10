package kr.co.seoulit.hisback.surgery.consent.controller;

import kr.co.seoulit.hisback.surgery.consent.dto.AnesthesiaConsentDto;
import kr.co.seoulit.hisback.surgery.consent.service.AnesthesiaConsentService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술/마취 동의서 컨트롤러 (SL2-38 확인기록 / SL2-53 확인기록 / SL2-54 조회, API-SUR-004)
 * anesthesia.controller.AnesthesiaRecordController에서 분리됨 (컴포넌트 분리: SL2-42 consent).
 */
@RestController
@RequestMapping("/api/v1/surgery/{surgeryId}")
@RequiredArgsConstructor
public class AnesthesiaConsentController {

    private final AnesthesiaConsentService anesthesiaConsentService;

    /** 수술/마취 동의서 확인 기록 작성 (SL2-38 / SL2-53 / API-SUR-004) */
    @PostMapping("/consent")
    public ApiResponse<AnesthesiaConsentDto> createConsent(@PathVariable Long surgeryId,
                                                             @RequestBody AnesthesiaConsentDto dto) {
        return ApiResponse.ok("동의서 확인 기록이 저장되었습니다.",
                anesthesiaConsentService.createConsent(surgeryId, dto));
    }

    /** 동의서 조회 (SL2-54) */
    @GetMapping("/consent")
    public ApiResponse<List<AnesthesiaConsentDto>> getConsents(@PathVariable Long surgeryId) {
        return ApiResponse.ok(anesthesiaConsentService.getConsents(surgeryId));
    }
}
