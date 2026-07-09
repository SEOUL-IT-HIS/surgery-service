package kr.co.seoulit.hisback.surgery.anesthesia.controller;

import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaConsentDto;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.service.AnesthesiaRecordService;
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
 * 마취기록 컨트롤러
 * (SL2-18 활력징후 기록 / SL2-21 약물투여기록 / SL2-34 조회 / SL2-38 동의서확인기록 / API-SUR-004)
 */
@RestController
@RequestMapping("/api/v1/surgery/{surgeryId}")
@RequiredArgsConstructor
public class AnesthesiaRecordController {

    private final AnesthesiaRecordService anesthesiaRecordService;

    /** 마취기록 작성 (SL2-18 / SL2-21) */
    @PostMapping("/anesthesia")
    public ApiResponse<AnesthesiaRecordDto> createRecord(@PathVariable Long surgeryId,
                                                         @RequestBody AnesthesiaRecordDto dto) {
        return ApiResponse.ok("마취기록이 저장되었습니다.",
                anesthesiaRecordService.createRecord(surgeryId, dto));
    }

    /** 마취기록 조회 (SL2-34) */
    @GetMapping("/anesthesia")
    public ApiResponse<List<AnesthesiaRecordDto>> getRecords(@PathVariable Long surgeryId) {
        return ApiResponse.ok(anesthesiaRecordService.getRecords(surgeryId));
    }

    /** 수술/마취 동의서 확인 기록 작성 (SL2-38 / SL2-53 / API-SUR-004) */
    @PostMapping("/consent")
    public ApiResponse<AnesthesiaConsentDto> createConsent(@PathVariable Long surgeryId,
                                                          @RequestBody AnesthesiaConsentDto dto) {
        return ApiResponse.ok("동의서 확인 기록이 저장되었습니다.",
                anesthesiaRecordService.createConsent(surgeryId, dto));
    }

    /** 동의서 조회 (SL2-54) */
    @GetMapping("/consent")
    public ApiResponse<List<AnesthesiaConsentDto>> getConsents(@PathVariable Long surgeryId) {
        return ApiResponse.ok(anesthesiaRecordService.getConsents(surgeryId));
    }
}
