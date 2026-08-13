package kr.co.seoulit.hisback.surgery.proceduremaster.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.proceduremaster.dto.SurgeryProcedureDto;
import kr.co.seoulit.hisback.surgery.proceduremaster.entity.SurgeryProcedure;
import kr.co.seoulit.hisback.surgery.proceduremaster.repository.SurgeryProcedureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술항목 마스터 서비스 구현체 (SL2-70 목록조회 / SL2-71 등록·수정)
 *
 * <p>수술항목은 Surgery Service 소유 업무마스터다. admin 공통코드가 아니므로 우리가
 * 직접 관리한다(SL2-71 요구사항 명시).</p>
 *
 * <p><b>삭제 API 를 두지 않는다</b> — 과거 수술기록이 {@code procedure_cd} 를 참조하므로
 * 행을 지우면 지난 기록의 술식명을 알 수 없게 된다. 쓰지 않는 항목은
 * {@code activeYn='N'} 으로 내린다(§21.6 삭제보다 상태 변경).</p>
 */
@Service
@Transactional(readOnly = true)
public class SurgeryProcedureServiceImpl implements SurgeryProcedureService {

    /** 사용 여부 기본값 — 등록하는 항목은 쓰려고 만드는 것이다 */
    private static final String ACTIVE_Y = "Y";

    private static final String ACTIVE_N = "N";

    private final SurgeryProcedureRepository surgeryProcedureRepository;

    public SurgeryProcedureServiceImpl(SurgeryProcedureRepository surgeryProcedureRepository) {
        this.surgeryProcedureRepository = surgeryProcedureRepository;
    }

    /**
     * SL2-70: 수술항목 마스터 목록 조회
     *
     * <p>사용 여부로 거르지 않고 전부 돌려준다 — 마스터 관리 화면은 미사용 항목도 보여야
     * 다시 살릴 수 있다. 수술 등록 화면처럼 '쓸 수 있는 것만' 필요한 곳이 생기면 그때
     * 별도 메서드를 둔다.</p>
     */
    @Override
    public List<SurgeryProcedureDto> getSurgeryProcedures() {
        return surgeryProcedureRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * SL2-70: 수술항목 단건 조회
     *
     * <p><b>null 대신 예외를 던진다.</b> 컨트롤러가 null 을 받아
     * {@code ResponseEntity.notFound().build()} 를 돌려주면 본문이 비어 나가는데,
     * 우리 응답은 모두 {@code ApiResponse{code,message,data}} 여야 한다(§11.3).
     * 본문이 없으면 프론트 resolveSurgeryMessage 가 띄울 문구를 못 찾는다.</p>
     */
    @Override
    public SurgeryProcedureDto getSurgeryProcedureById(String procedureCd) {
        return toDto(findOrThrow(procedureCd));
    }

    /**
     * SL2-71: 수술항목 등록
     *
     * <p>코드는 사용자가 정하는 마스터 코드라 서버가 채번하지 않는다. 그래서 <b>중복이
     * 실제로 발생</b>하고, 막지 않으면 기존 항목을 덮어쓴다({@code save} 는 PK 가 있으면
     * update 로 동작한다). 조용한 덮어쓰기가 가장 위험하므로 먼저 막는다.</p>
     *
     * <p>{@code activeYn} 을 안 보내면 'Y' 로 채운다 — 등록하는 항목은 쓰려고 만드는 것이고,
     * 컬럼이 NOT NULL 이라 비워두면 제약 위반이 난다.</p>
     */
    @Override
    @Transactional
    public SurgeryProcedureDto createSurgeryProcedure(SurgeryProcedureDto request) {
        String procedureCd = requireCode(request.getProcedureCd());

        if (surgeryProcedureRepository.existsById(procedureCd)) {
            throw new BusinessException(ErrorCode.PROCEDURE_DUPLICATED, procedureCd);
        }

        SurgeryProcedure entity =
                SurgeryProcedure.builder()
                        .procedureCd(procedureCd)
                        .procedureName(request.getProcedureName())
                        .activeYn(normalizeActiveYn(request.getActiveYn(), ACTIVE_Y))
                        .build();

        return toDto(surgeryProcedureRepository.save(entity));
    }

    /**
     * SL2-71: 수술항목 수정 (전체 교체)
     *
     * <p>코드는 PK 라 바꿀 수 없다. 코드를 바꾸는 것은 다른 항목을 만드는 일이고, 과거
     * 수술기록이 옛 코드를 참조하고 있어 바꾸면 그 기록이 끊긴다.</p>
     *
     * <p>{@code activeYn} 을 안 보내면 <b>기존 값을 유지</b>한다. 전체 교체지만 사용 여부까지
     * 초기화하면 이름만 고치려다 내려둔 항목이 되살아난다.</p>
     */
    @Override
    @Transactional
    public SurgeryProcedureDto updateSurgeryProcedure(SurgeryProcedureDto request) {
        SurgeryProcedure entity = findOrThrow(requireCode(request.getProcedureCd()));

        entity.setProcedureName(request.getProcedureName());
        entity.setActiveYn(normalizeActiveYn(request.getActiveYn(), entity.getActiveYn()));

        return toDto(surgeryProcedureRepository.save(entity));
    }

    /**
     * SL2-71: 수술항목 부분 수정
     *
     * <p>보낸 항목만 바꾼다. 실제로는 <b>사용 여부 토글</b>에 주로 쓴다 — 목록에서 항목을
     * 내리거나 되살릴 때 이름까지 다시 보낼 이유가 없다.</p>
     *
     * <p>{@code update} 와 나눠 둔 이유 — 전체 교체(PUT)는 보내지 않은 값을 비우는 것이
     * 계약이고, 부분 수정(PATCH)은 건드리지 않는 것이 계약이다(§21.8). 한 메서드가 둘 다
     * 하려 들면 "안 보낸 값을 어떻게 할지"가 호출부마다 달라진다.</p>
     */
    @Override
    @Transactional
    public SurgeryProcedureDto patchSurgeryProcedure(SurgeryProcedureDto request) {
        SurgeryProcedure entity = findOrThrow(requireCode(request.getProcedureCd()));

        if (request.getProcedureName() != null && !request.getProcedureName().isBlank()) {
            entity.setProcedureName(request.getProcedureName());
        }
        if (request.getActiveYn() != null && !request.getActiveYn().isBlank()) {
            entity.setActiveYn(normalizeActiveYn(request.getActiveYn(), entity.getActiveYn()));
        }

        return toDto(surgeryProcedureRepository.save(entity));
    }

    /** 없으면 404 SUR055. 조회·수정이 공통으로 쓴다. */
    private SurgeryProcedure findOrThrow(String procedureCd) {
        return surgeryProcedureRepository
                .findById(procedureCd)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROCEDURE_NOT_FOUND, procedureCd));
    }

    /** 코드가 비어 있으면 조회할 것도 저장할 것도 없다. */
    private String requireCode(String procedureCd) {
        if (procedureCd == null || procedureCd.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "procedureCd 누락");
        }
        return procedureCd;
    }

    /**
     * 사용 여부를 'Y'/'N' 로 맞춘다 (§14.2 `_yn` = CHAR(1), 'Y'/'N' 만 허용).
     *
     * <p>비어 있으면 {@code fallback} 을 쓴다 — 등록이면 'Y', 수정이면 기존 값이다.
     * 'Y' 가 아닌 값은 전부 'N' 으로 본다. 'y'·'true' 같은 변형을 그대로 저장하면
     * 조회 조건이 어긋나기 시작한다.</p>
     */
    private String normalizeActiveYn(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return (fallback == null || fallback.isBlank()) ? ACTIVE_Y : fallback;
        }
        return ACTIVE_Y.equalsIgnoreCase(value) ? ACTIVE_Y : ACTIVE_N;
    }

    /**
     * 엔티티 → DTO 변환
     *
     * <p>필드 순서는 DTO 선언 순서와 같아야 한다. {@code @AllArgsConstructor} 가 만든 생성자는
     * 이름이 아니라 <b>순서</b>로 값을 받기 때문에, 순서가 어긋나면 컴파일은 되는데 값이
     * 뒤바뀐다(같은 String 타입끼리는 컴파일러도 못 잡는다).</p>
     */
    private SurgeryProcedureDto toDto(SurgeryProcedure p) {
        return new SurgeryProcedureDto(
                p.getProcedureCd(),
                p.getProcedureName(),
                p.getActiveYn(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
