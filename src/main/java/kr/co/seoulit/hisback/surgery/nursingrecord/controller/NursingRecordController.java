package kr.co.seoulit.hisback.surgery.nursingrecord.controller;

import java.util.List;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.nursingrecord.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursingrecord.service.NursingRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술간호기록 컨트롤러 (SL2-58 작성 / SL2-59 물품카운트 / SL2-60 검체관리 / SL2-61 조회)
 */
@RestController
@RequestMapping("/api/v1/surgery")
public class NursingRecordController {

    private final NursingRecordService nursingRecordService;

    public NursingRecordController(NursingRecordService nursingRecordService) {
        this.nursingRecordService = nursingRecordService;
    }

    @GetMapping("/{surgeryId}/nursing-records")
    public ResponseEntity<ApiResponse<List<NursingRecordDto>>> getNursingRecords(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(nursingRecordService.getNursingRecords(surgeryId)));
    }

    @PostMapping("/{surgeryId}/nursing-records")
    public ResponseEntity<ApiResponse<NursingRecordDto>> createNursingRecord(
            @PathVariable String surgeryId, @RequestBody NursingRecordDto request) {
        request.setSurgeryId(surgeryId);
        NursingRecordDto created = nursingRecordService.createNursingRecord(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }
}
