package kr.co.seoulit.hisback.surgery.checklist.controller;

import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.service.SurgeryChecklistService;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
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
 * 수술안전체크리스트 컨트롤러
 * (SL2-22 작성 / SL2-23 사후수정 / SL2-24 업로드 / SL2-35 조회 / API-SUR-003)
 */
@RestController
@RequestMapping("/api/v1/surgery/{surgeryId}/checklist")
@RequiredArgsConstructor
public class SurgeryChecklistController {

    private final SurgeryChecklistService surgeryChecklistService;

    /** 단계별 체크리스트 작성/제출 (SL2-22 / API-SUR-003) */
    @PostMapping
    public ApiResponse<SurgeryChecklistDto> submit(@PathVariable Long surgeryId,
                                                   @RequestBody SurgeryChecklistDto dto) {
        return ApiResponse.ok("체크리스트가 저장되었습니다.",
                surgeryChecklistService.submitPhase(surgeryId, dto));
    }

    /** 체크리스트 사후 수정 (SL2-23) */
    @PutMapping("/{checklistId}")
    public ApiResponse<SurgeryChecklistDto> update(@PathVariable Long surgeryId,
                                                   @PathVariable Long checklistId,
                                                   @RequestBody SurgeryChecklistDto dto) {
        return ApiResponse.ok("체크리스트가 수정되었습니다.",
                surgeryChecklistService.update(checklistId, dto));
    }

    /** 체크리스트 조회 (SL2-35) */
    @GetMapping
    public ApiResponse<List<SurgeryChecklistDto>> list(@PathVariable Long surgeryId) {
        return ApiResponse.ok(surgeryChecklistService.getChecklists(surgeryId));
    }
}
