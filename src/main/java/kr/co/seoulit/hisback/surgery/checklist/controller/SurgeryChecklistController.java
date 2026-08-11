package kr.co.seoulit.hisback.surgery.checklist.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.service.SurgeryChecklistService;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 수술안전체크리스트 컨트롤러 (SL2-46 SignIn / SL2-47 TimeOut / SL2-48 SignOut / SL2-49 사후수정 / SL2-35 조회)
 *
 * <p>체크리스트는 항상 특정 수술에 속하므로 경로를 {@code /{surgeryId}/checklist} 로 중첩했다.
 * 반면 단건 수정은 체크리스트 ID만으로 대상이 정해지므로 {@code /checklist/{checklistId}} 로
 * 평평하게 뒀다 — 어느 수술 것인지 몰라도 수정할 수 있어야 하기 때문이다(§21.8).</p>
 *
 * <p>단계 순서 검증(SignIn→TimeOut→SignOut)은 여기가 아니라 서비스가 한다. 컨트롤러는
 * 경로와 값 변환만 책임진다.</p>
 */
@RestController
@RequestMapping("/api/surgery")
public class SurgeryChecklistController {

    private final SurgeryChecklistService surgeryChecklistService;

    public SurgeryChecklistController(SurgeryChecklistService surgeryChecklistService) {
        this.surgeryChecklistService = surgeryChecklistService;
    }

    //getChecklist는 수술 ID로 해당 수술의 체크리스트 전체를 조회해 ApiResponse로 반환한다 (SL2-35)
    @GetMapping("/{surgeryId}/checklist")
    public ResponseEntity<ApiResponse<List<SurgeryChecklistDto>>> getChecklist(
            @PathVariable String surgeryId) {
        return ResponseEntity.ok(ApiResponse.success(surgeryChecklistService.getChecklist(surgeryId)));
    }

    //createChecklistItem은 POST 요청을 받아 SurgeryChecklistService에 전달(위임)하고, 결과를 ApiResponse로 감싸 반환한다
    @PostMapping("/{surgeryId}/checklist")
    public ResponseEntity<ApiResponse<SurgeryChecklistDto>> createChecklistItem(
            @PathVariable String surgeryId, @Valid @RequestBody SurgeryChecklistDto request) {
        // 본문의 surgeryId 는 신뢰하지 않고 경로 값으로 덮어쓴다.
        // 둘이 다르면 어느 수술에 붙일지 모호해지므로 경로를 정본으로 삼는다.
        request.setSurgeryId(surgeryId);
        SurgeryChecklistDto created = surgeryChecklistService.createChecklistItem(request);
        // 새 자원을 만들었으므로 200이 아니라 201 Created 로 응답한다
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateChecklistItem은 완료 여부만 바꾸므로 PUT이 아니라 PATCH를 쓴다 (SL2-49)
    @PatchMapping("/checklist/{checklistId}")
    public ResponseEntity<ApiResponse<SurgeryChecklistDto>> updateChecklistItem(
            @PathVariable String checklistId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgeryChecklistService.updateChecklistItem(checklistId, request.get("completedYn"))));
    }
}
