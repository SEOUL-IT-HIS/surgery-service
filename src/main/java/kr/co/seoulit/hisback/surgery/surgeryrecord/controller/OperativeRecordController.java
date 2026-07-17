package kr.co.seoulit.hisback.surgery.surgeryrecord.controller;

import java.util.List;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.surgeryrecord.dto.OperativeRecordDto;
import kr.co.seoulit.hisback.surgery.surgeryrecord.service.OperativeRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술기록 컨트롤러 (SL2-55 작성 / SL2-56 수정 / SL2-57 조회)
 */
@RestController
@RequestMapping("/api/v1/surgery")
public class OperativeRecordController {

    private final OperativeRecordService operativeRecordService;

    public OperativeRecordController(OperativeRecordService operativeRecordService) {
        this.operativeRecordService = operativeRecordService;
    }

    @GetMapping("/{surgeryId}/operative-record")
    public ResponseEntity<ApiResponse<List<OperativeRecordDto>>> getOperativeRecords(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(operativeRecordService.getOperativeRecords(surgeryId)));
    }

    @PostMapping("/{surgeryId}/operative-record")
    public ResponseEntity<ApiResponse<OperativeRecordDto>> createOperativeRecord(
            @PathVariable String surgeryId, @RequestBody OperativeRecordDto request) {
        request.setSurgeryId(surgeryId);
        OperativeRecordDto created = operativeRecordService.createOperativeRecord(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    @PutMapping("/operative-record/{recordId}")
    public ResponseEntity<ApiResponse<OperativeRecordDto>> updateOperativeRecord(
            @PathVariable String recordId, @RequestBody OperativeRecordDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(operativeRecordService.updateOperativeRecord(recordId, request)));
    }
}
