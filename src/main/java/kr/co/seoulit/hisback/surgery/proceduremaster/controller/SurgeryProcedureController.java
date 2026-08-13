package kr.co.seoulit.hisback.surgery.proceduremaster.controller;

import java.util.List;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;
import kr.co.seoulit.hisback.surgery.proceduremaster.service.SurgeryProcedureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술항목 마스터 컨트롤러 (SL2-70 목록조회 / SL2-71 등록)
 *
 * <p>수술항목은 Surgery Service 소유 업무마스터다. admin 공통코드가 아니므로 우리가
 * 직접 관리한다(SL2-71 요구사항 명시).</p>
 *
 * <p>경로를 복수형({@code /procedures})으로 둔 이유 — 수술실({@code /rooms})·장비
 * ({@code /equipment}) 와 같은 자리의 마스터라 결을 맞춘다.</p>
 */
@RestController
@RequestMapping("/api/surgery/procedures")
public class SurgeryProcedureController {

    private final SurgeryProcedureService surgeryProcedureService;

    public SurgeryProcedureController(SurgeryProcedureService surgeryProcedureService) {
        this.surgeryProcedureService = surgeryProcedureService;
    }

    /**
     * SL2-70: 수술항목 마스터 목록 조회
     *
     * <p>{@code GET /api/surgery/procedures}</p>
     *
     * <p>사용 여부와 무관하게 전부 돌려준다 — 마스터 관리 화면은 미사용 항목도 보여야
     * 다시 살릴 수 있다. 페이징은 아직 붙이지 않았다. 수술항목이 수백 개로 늘면
     * 수술실 목록처럼 PageableSupport 를 쓰면 된다.</p>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgeryProcedureDto>>> getSurgeryProcedures() {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryProcedureService.getSurgeryProcedure()));
    }
}
