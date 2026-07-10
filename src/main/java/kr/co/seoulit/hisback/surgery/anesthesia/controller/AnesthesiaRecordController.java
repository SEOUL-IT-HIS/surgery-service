package kr.co.seoulit.hisback.surgery.anesthesia.controller;

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
 * 마취기록 컨트롤러 (SL2-18 활력징후 기록 / SL2-21 약물투여기록 / SL2-34 조회)
 * ※ 동의서(SL2-38 / SL2-53 / SL2-54) 엔드포인트는 consent.controller.AnesthesiaConsentController로 분리됨
 *   (컴포넌트 분리: SL2-42 consent)
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
}
