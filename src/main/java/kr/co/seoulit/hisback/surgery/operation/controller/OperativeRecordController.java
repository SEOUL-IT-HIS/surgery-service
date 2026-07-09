package kr.co.seoulit.hisback.surgery.operation.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.operation.dto.OperativeRecordDto;
import kr.co.seoulit.hisback.surgery.operation.service.OperativeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술기록지 컨트롤러 (SL2-55 작성 / SL2-56 수정 / SL2-57 조회 / API-SUR-005)
 */
@RestController
@RequestMapping("/api/v1/surgery/{surgeryId}/operative-record")
@RequiredArgsConstructor
public class OperativeRecordController {

    private final OperativeRecordService operativeRecordService;

    /** 수술기록지 작성 (SL2-55 / API-SUR-005) */
    @PostMapping
    public ApiResponse<OperativeRecordDto> create(@PathVariable Long surgeryId,
                                                  @RequestBody OperativeRecordDto dto) {
        return ApiResponse.ok("수술기록지가 저장되었습니다.", operativeRecordService.create(surgeryId, dto));
    }

    /** 수술기록지 수정 (SL2-56) */
    @PutMapping("/{recordId}")
    public ApiResponse<OperativeRecordDto> update(@PathVariable Long surgeryId,
                                                  @PathVariable Long recordId,
                                                  @RequestBody OperativeRecordDto dto) {
        return ApiResponse.ok("수술기록지가 수정되었습니다.", operativeRecordService.update(recordId, dto));
    }

    /** 수술기록지 조회 (SL2-57) */
    @GetMapping
    public ApiResponse<List<OperativeRecordDto>> list(@PathVariable Long surgeryId) {
        return ApiResponse.ok(operativeRecordService.getRecords(surgeryId));
    }
}
