package kr.co.seoulit.hisback.surgery.surgeryrecord.controller;

import java.util.List;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.surgeryrecord.dto.OperativeRecordDto;
import kr.co.seoulit.hisback.surgery.surgeryrecord.service.OperativeRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술기록 컨트롤러 (SL2-55 작성 / SL2-56 수정 / SL2-57 조회)
 *
 * <p>경로가 두 갈래인 이유 — 작성·목록조회는 어느 수술의 기록인지가 필요해
 * {@code /{surgeryId}/operative-record} 로 중첩하고, 단건조회·수정은 기록 ID만으로 대상이
 * 정해지므로 {@code /operative-record/{recordId}} 로 평평하게 뒀다(§21.8).</p>
 *
 * <p>수정에 PATCH 가 아니라 PUT 을 쓴 이유 — 시술 코드·명칭을 통째로 교체하는 성격이라
 * 부분 변경이 아니다. 상태만 바꾸는 API 가 필요해지면 그때 PATCH 를 따로 낸다.</p>
 */
@RestController
@RequestMapping("/api/surgery")
public class OperativeRecordController {

    private final OperativeRecordService operativeRecordService;

    public OperativeRecordController(OperativeRecordService operativeRecordService) {
        this.operativeRecordService = operativeRecordService;
    }

    //getOperativeRecords는 수술 ID로 해당 수술의 수술기록 목록을 조회해 ApiResponse로 반환한다 (SL2-57)
    @GetMapping("/{surgeryId}/operative-record")
    public ResponseEntity<ApiResponse<List<OperativeRecordDto>>> getOperativeRecords(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(operativeRecordService.getOperativeRecords(surgeryId)));
    }

    //getOperativeRecord는 기록 ID 하나로 수술기록 단건을 조회해 ApiResponse로 반환한다 (SL2-57)
    // 위 목록 조회와 경로가 겹쳐 보이지만, 두 번째 칸이 고정 문자열(operative-record)이냐
    // 값이냐로 갈려 충돌하지 않는다.
    //   GET /api/surgery/SUR-1/operative-record  → 목록
    //   GET /api/surgery/operative-record/OR-1   → 이 메서드
    // 아래 PUT /operative-record/{recordId} 와 같은 자리를 쓰지만 방식(GET/PUT)이 달라 별개다.
    @GetMapping("/operative-record/{recordId}")
    public ResponseEntity<ApiResponse<OperativeRecordDto>> getOperativeRecord(
            @PathVariable String recordId) {
        return ResponseEntity.ok(ApiResponse.success(operativeRecordService.getOperativeRecord(recordId)));
    }

    //createOperativeRecord는 POST 요청을 받아 OperativeRecordService에 전달(위임)하고, 결과를 ApiResponse로 감싸 반환한다 (SL2-55)
    @PostMapping("/{surgeryId}/operative-record")
    public ResponseEntity<ApiResponse<OperativeRecordDto>> createOperativeRecord(
            @PathVariable String surgeryId, @RequestBody OperativeRecordDto request) {
        // 본문의 surgeryId 는 신뢰하지 않고 경로 값으로 덮어쓴다 — 둘이 다르면 경로가 정본이다
        request.setSurgeryId(surgeryId);
        OperativeRecordDto created = operativeRecordService.createOperativeRecord(request);
        // 새 자원을 만들었으므로 201 Created
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateOperativeRecord는 PUT 요청을 받아 기록 내용을 교체한다 (SL2-56)
    @PutMapping("/operative-record/{recordId}")
    public ResponseEntity<ApiResponse<OperativeRecordDto>> updateOperativeRecord(
            @PathVariable String recordId, @RequestBody OperativeRecordDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(operativeRecordService.updateOperativeRecord(recordId, request)));
    }
}
