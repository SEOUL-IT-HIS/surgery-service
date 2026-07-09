package kr.co.seoulit.hisback.surgery.nursing.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.nursing.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursing.service.NursingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술간호기록 컨트롤러 (SL2-58 작성 / SL2-59 물품카운트 / SL2-60 검체 / SL2-61 조회)
 */
@RestController
@RequestMapping("/api/v1/surgery/{surgeryId}/nursing-record")
@RequiredArgsConstructor
public class NursingRecordController {

    private final NursingRecordService nursingRecordService;

    /** 수술간호기록 작성 (SL2-58 / SL2-60) */
    @PostMapping
    public ApiResponse<NursingRecordDto> create(@PathVariable Long surgeryId,
                                                @RequestBody NursingRecordDto dto) {
        return ApiResponse.ok("수술간호기록이 저장되었습니다.", nursingRecordService.create(surgeryId, dto));
    }

    /** 수술간호기록 수정 */
    @PutMapping("/{nursingRecordId}")
    public ApiResponse<NursingRecordDto> update(@PathVariable Long surgeryId,
                                                @PathVariable Long nursingRecordId,
                                                @RequestBody NursingRecordDto dto) {
        return ApiResponse.ok("수술간호기록이 수정되었습니다.", nursingRecordService.update(nursingRecordId, dto));
    }

    /** 물품 카운트 대조 (SL2-59 / BR-013) */
    @PatchMapping("/{nursingRecordId}/count")
    public ApiResponse<NursingRecordDto> recordCount(@PathVariable Long surgeryId,
                                                     @PathVariable Long nursingRecordId,
                                                     @RequestParam Integer countInitial,
                                                     @RequestParam Integer countFinal,
                                                     @RequestParam(required = false) String remark,
                                                     @RequestParam(defaultValue = "false") boolean resolved) {
        return ApiResponse.ok("물품 카운트가 기록되었습니다.",
                nursingRecordService.recordCount(nursingRecordId, countInitial, countFinal, remark, resolved));
    }

    /** 수술간호기록 조회 (SL2-61) */
    @GetMapping
    public ApiResponse<List<NursingRecordDto>> list(@PathVariable Long surgeryId) {
        return ApiResponse.ok(nursingRecordService.getRecords(surgeryId));
    }
}
