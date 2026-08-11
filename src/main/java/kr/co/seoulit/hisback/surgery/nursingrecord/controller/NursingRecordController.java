package kr.co.seoulit.hisback.surgery.nursingrecord.controller;

import java.util.List;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.nursingrecord.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursingrecord.service.NursingRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술간호기록 컨트롤러 (SL2-58 작성 / SL2-59 물품카운트 / SL2-60 검체관리 / SL2-61 조회)
 *
 * <p>물품카운트·검체관리에 별도 엔드포인트를 두지 않은 이유 — 셋 다 간호기록 한 행의
 * 필드라서 작성 API 하나로 함께 저장된다. 나중에 각각 따로 갱신할 일이 생기면
 * {@code PATCH /nursing-records/{id}/item-count} 처럼 나누면 된다(§21.8).</p>
 *
 * <p>간호기록은 항상 특정 수술에 속하므로 경로를 {@code /{surgeryId}/nursing-records} 로 중첩했다.</p>
 */
@RestController
@RequestMapping("/api/surgery")
public class NursingRecordController {

    private final NursingRecordService nursingRecordService;

    public NursingRecordController(NursingRecordService nursingRecordService) {
        this.nursingRecordService = nursingRecordService;
    }

    //getNursingRecords는 수술 ID로 해당 수술의 간호기록 목록을 조회해 ApiResponse로 반환한다 (SL2-61)
    @GetMapping("/{surgeryId}/nursing-records")
    public ResponseEntity<ApiResponse<List<NursingRecordDto>>> getNursingRecords(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(nursingRecordService.getNursingRecords(surgeryId)));
    }

    //createNursingRecord는 POST 요청을 받아 NursingRecordService에 전달(위임)하고, 결과를 ApiResponse로 감싸 반환한다 (SL2-58)
    @PostMapping("/{surgeryId}/nursing-records")
    public ResponseEntity<ApiResponse<NursingRecordDto>> createNursingRecord(
            @PathVariable String surgeryId, @RequestBody NursingRecordDto request) {
        // 본문의 surgeryId 는 신뢰하지 않고 경로 값으로 덮어쓴다 — 둘이 다르면 경로가 정본이다
        request.setSurgeryId(surgeryId);
        NursingRecordDto created = nursingRecordService.createNursingRecord(request);
        // 새 자원을 만들었으므로 201 Created
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }
}
