package kr.co.seoulit.hisback.surgery.proceduremaster.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;
import kr.co.seoulit.hisback.surgery.proceduremaster.service.SurgeryProcedureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술항목 마스터 컨트롤러 (SL2-70 목록·단건 조회 / SL2-71 등록·수정)
 *
 * <p>수술항목은 Surgery Service 소유 업무마스터다. admin 공통코드가 아니므로 우리가
 * 직접 관리한다(SL2-71 요구사항 명시).</p>
 *
 * <p>경로를 복수형({@code /procedures})으로 둔 이유 — 수술실({@code /rooms})·장비
 * ({@code /equipment}) 와 같은 자리의 마스터라 결을 맞춘다.</p>
 *
 * <p><b>삭제(DELETE)를 열지 않는다</b> — 과거 수술기록이 코드를 참조하므로 지우면
 * 지난 기록의 술식명을 알 수 없게 된다. 쓰지 않는 항목은 PATCH 로 내린다(§21.6, §21.8).</p>
 *
 * <p>없는 코드 조회·수정은 서비스가 404 SUR055 를 던진다. 컨트롤러가
 * {@code ResponseEntity.notFound()} 를 쓰지 않는 이유는 그것이 본문 없는 응답이라
 * 우리 공통 응답 형식(§11.3)을 벗어나기 때문이다.</p>
 */
@RestController
@RequestMapping("/api/surgery/procedures")
public class SurgeryProcedureController {

    private final SurgeryProcedureService surgeryProcedureService;

    public SurgeryProcedureController(SurgeryProcedureService surgeryProcedureService) {
        this.surgeryProcedureService = surgeryProcedureService;
    }

    /**
     * SL2-70: 수술항목 목록 조회
     *
     * <p>{@code GET /api/surgery/procedures}</p>
     *
     * <p>사용 여부와 무관하게 전부 돌려준다 — 마스터 관리 화면은 미사용 항목도 보여야
     * 다시 살릴 수 있다. 페이징은 아직 붙이지 않았다. 수백 개로 늘면 수술실 목록처럼
     * PageableSupport 를 쓰면 된다.</p>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgeryProcedureDto>>> getSurgeryProcedures() {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryProcedureService.getSurgeryProcedures()));
    }

    /**
     * SL2-70: 수술항목 단건 조회
     *
     * <p>{@code GET /api/surgery/procedures/{procedureCd}}</p>
     *
     * <p>경로 변수 이름과 파라미터 이름이 같아야 Spring 이 값을 넣어준다. 다르면
     * {@code @PathVariable("이름")} 으로 짝을 지어야 하고, 짝이 없으면 기동 시점에 실패한다.</p>
     */
    @GetMapping("/{procedureCd}")
    public ResponseEntity<ApiResponse<SurgeryProcedureDto>> getSurgeryProcedure(
            @PathVariable String procedureCd) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryProcedureService.getSurgeryProcedureById(procedureCd)));
    }

    /**
     * SL2-71: 수술항목 등록
     *
     * <p>{@code POST /api/surgery/procedures}</p>
     *
     * <p>등록은 201 로 돌려준다 — 다른 등록 API(수술·동의서)와 같은 규칙이다.</p>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SurgeryProcedureDto>> createSurgeryProcedure(
            @Valid @RequestBody SurgeryProcedureDto request) {
        SurgeryProcedureDto created = surgeryProcedureService.createSurgeryProcedure(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    /**
     * SL2-71: 수술항목 수정 (전체 교체)
     *
     * <p>{@code PUT /api/surgery/procedures/{procedureCd}}</p>
     *
     * <p>코드는 경로에서 받아 본문 값을 덮어쓴다. 본문에 다른 코드가 실려 와도 경로가
     * 가리키는 항목을 고친다 — 주소가 대상을 정한다는 원칙이 더 분명하다.</p>
     */
    @PutMapping("/{procedureCd}")
    public ResponseEntity<ApiResponse<SurgeryProcedureDto>> updateSurgeryProcedure(
            @PathVariable String procedureCd, @Valid @RequestBody SurgeryProcedureDto request) {
        request.setProcedureCd(procedureCd);
        return ResponseEntity.ok(
                ApiResponse.success(surgeryProcedureService.updateSurgeryProcedure(request)));
    }

    /**
     * SL2-71: 수술항목 부분 수정 (주로 사용 여부 토글)
     *
     * <p>{@code PATCH /api/surgery/procedures/{procedureCd}}<br>
     * 예: {@code {"activeYn":"N"}} 으로 항목을 내린다.</p>
     *
     * <p>{@code @Valid} 를 걸지 않는다 — 부분 수정이라 이름을 안 보내는 것이 정상인데,
     * DTO 의 {@code @NotBlank} 가 그것을 막는다.</p>
     */
    @PatchMapping("/{procedureCd}")
    public ResponseEntity<ApiResponse<SurgeryProcedureDto>> patchSurgeryProcedure(
            @PathVariable String procedureCd, @RequestBody SurgeryProcedureDto request) {
        request.setProcedureCd(procedureCd);
        return ResponseEntity.ok(
                ApiResponse.success(surgeryProcedureService.patchSurgeryProcedure(request)));
    }
}
