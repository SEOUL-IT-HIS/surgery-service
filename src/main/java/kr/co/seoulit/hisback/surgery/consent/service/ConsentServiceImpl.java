package kr.co.seoulit.hisback.surgery.consent.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.consent.dto.ConsentDto;
import kr.co.seoulit.hisback.surgery.consent.entity.Consent;
import kr.co.seoulit.hisback.surgery.consent.repository.ConsentRepository;
import kr.co.seoulit.hisback.surgery.common.cache.CommonCodeCache;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryGuard;
import org.springframework.stereotype.Service;

/**
 * 동의서 관리 서비스 구현체 (SL2-42)
 *
 * <p>수정(update) API가 없는 이유 — 동의서는 서명 시점의 사실 기록이라 고쳐 쓰지 않는다.
 * 내용이 바뀌면 기존 행을 수정하는 게 아니라 새로 동의를 받아 다른 행으로 남긴다(§21.6).
 * 그래서 이 클래스에는 조회와 생성만 있다.</p>
 *
 * <p>동의서 파일(스캔본·이미지) 자체는 이 서비스가 보관하지 않는다. 시스템은 '누가·언제·
 * 어떤 종류에 동의했는가'만 저장한다(§21.5). 실물 문서는 문서관리 소관이다.</p>
 */
@Service
public class ConsentServiceImpl implements ConsentService {

    /**
     * 동의서 종류 코드 그룹.
     *
     * <p>admin 에 이미 등록돼 있다 — 01 수술 / 02 마취 / 03 비용견적.
     * 검사·영상이 같은 그룹에 CONTRAST·INVASIVE 를 함께 쓰고 있어, 우리 값만 골라
     * 검증하지 않고 그룹 전체를 인정한다. 공용 그룹을 나눠 쓰는 것은 admin 소관이다(§21.4).</p>
     */
    private static final String GROUP_CONSENT_TYPE = "CONSENT_TYPE_CD";

    private final ConsentRepository consentRepository;

    /** SL2-223: 하위 목록 조회 전에 수술 존재를 확인한다 */
    private final SurgeryGuard surgeryGuard;

    /** 동의서 종류가 admin 에 등록된 값인지 확인하는 데 쓴다 */
    private final CommonCodeCache commonCodeCache;

    // 생성자가 하나뿐이라 @Autowired 없이도 Spring이 의존성을 주입한다
    public ConsentServiceImpl(
            ConsentRepository consentRepository,
            SurgeryGuard surgeryGuard,
            CommonCodeCache commonCodeCache) {
        this.consentRepository = consentRepository;
        this.surgeryGuard = surgeryGuard;
        this.commonCodeCache = commonCodeCache;
    }

    /**
     * SL2-54: 특정 수술의 동의서 목록
     *
     * <p>SL2-223: 수술 존재를 먼저 확인한다. 예전에는 "읽기에 불필요한 결합을 만들지 않는다"는
     * 이유로 확인하지 않았는데, 그러면 식별자를 잘못 넣은 것과 정말 동의서가 없는 것이
     * 구분되지 않는다. 화면은 "동의서가 없습니다"를 띄우고 사용자는 등록하러 가지만 실제로는
     * 존재하지 않는 수술을 보고 있게 된다. 조회가 한 번 늘어나는 비용보다 이 사고가 크다.
     * (2026-08-12 결정 — 서비스마다 갈려 있던 판단을 404 로 통일)</p>
     */
    @Override
    public List<ConsentDto> getConsents(String surgeryId) {
        surgeryGuard.requireExists(surgeryId);
        return consentRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** SL2-222: 환자별 동의서 이력 — 환자 존재 여부는 patient-service 소관이라 여기서 확인하지 않는다(§21.1). */
    @Override
    public List<ConsentDto> getConsentsByPatient(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "patientId 누락");
        }
        return consentRepository.findByPatientId(patientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 동의서 단건 조회
     *
     * <p>목록과 달리 없으면 예외를 던진다 — 특정 건을 지목한 요청이라 빈 결과가
     * 정상 응답일 수 없다.</p>
     */
    @Override
    public ConsentDto getConsent(String consentId) {
        return toDto(
                consentRepository
                        .findById(consentId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CONSENT_NOT_FOUND, consentId)));
    }

    /**
     * SL2-53: 동의 확인 기록 — <b>체크 한 번이 곧 이 호출이다.</b>
     *
     * <h3>중복 등록 거절에서 갱신으로 바꾼 이유</h3>
     *
     * <p>예전에는 같은 수술·같은 종류가 이미 있으면 SUR044 로 거절했다. 동의서가
     * 서명 시점의 사실 기록이라 덮어쓰면 안 된다는 판단이었고, 서명자·서명일을
     * 저장하던 시절에는 맞는 규칙이었다.</p>
     *
     * <p>지금은 남기는 것이 {@code signedYn} 하나뿐이라 사정이 다르다. 화면이
     * 체크박스가 되면서 <b>체크를 되돌리는 일</b>이 정상 동작에 포함됐는데,
     * 거절 규칙을 그대로 두면 잘못 누른 체크를 풀 방법이 없다. 행을 지우는 것은
     * 더 나쁘다 — "받은 적 없음"과 "받았다가 취소함"이 구분되지 않는다(§21.6).</p>
     *
     * <p>그래서 <b>있으면 갱신, 없으면 생성</b>한다. 체크와 해제가 같은 호출이고,
     * 같은 값을 두 번 보내도 결과가 같다.</p>
     */
    @Override
    public ConsentDto createConsent(ConsentDto request) {
        // SL2-216: 대상 수술이 실제로 있는지 먼저 본다.
        //
        //   없으면 404 SUR035. 이 검사가 없던 동안에는 오타로 잘못된 surgery_id 를 보내도
        //   동의서가 그대로 저장됐고, 그 행은 어느 수술에도 붙지 않은 채 남았다.
        //   조회 쪽은 이미 SurgeryGuard 를 쓰고 있었는데 등록만 빠져 있었다.
        surgeryGuard.requireExists(request.getSurgeryId());

        // 동의서 종류가 admin 에 등록된 값인지 확인한다(2026-08-25 연결).
        //
        //   그룹이 없으면 건너뛴다 — 다른 코드 검증과 같은 방식이다. 종류를 아예 안 보내는
        //   경우도 막지 않는다. 필수 여부는 DTO 검증이 정할 일이지 코드값 검증의 몫이 아니다.
        if (request.getConsentTypeCd() != null
                && !request.getConsentTypeCd().isBlank()
                && commonCodeCache.hasGroup(GROUP_CONSENT_TYPE)
                && !commonCodeCache.isValid(GROUP_CONSENT_TYPE, request.getConsentTypeCd())) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    GROUP_CONSENT_TYPE + "=" + request.getConsentTypeCd());
        }

        // 안 보내면 체크로 본다 — 이 API 를 부르는 대부분이 체크다(ConsentDto 참고)
        String signedYn = "N".equals(request.getSignedYn()) ? "N" : "Y";

        // 이미 있으면 플래그만 갈아끼운다. consent_id 와 created_at 은 그대로 남아
        // "언제 처음 체크했는지"가 보존되고, updated_at 이 마지막 변경 시각이 된다.
        Consent existing =
                consentRepository
                        .findBySurgeryIdAndConsentTypeCd(
                                request.getSurgeryId(), request.getConsentTypeCd())
                        .orElse(null);
        if (existing != null) {
            existing.setSignedYn(signedYn);
            return toDto(consentRepository.save(existing));
        }

        // PK는 서버가 채번한다. 프론트가 보낸 값이 있으면 존중하되(재시도·마이그레이션 대비),
        // 없으면 UUID로 생성한다 — surgery_id 처럼 업무 의미가 있는 코드가 아니라 내부 식별자다(§14.2)
        String consentId =
                request.getConsentId() != null ? request.getConsentId() : UUID.randomUUID().toString();
        Consent consent =
                Consent.builder()
                        .consentId(consentId)
                        .surgeryId(request.getSurgeryId())
                        .authorStaffId(request.getAuthorStaffId())
                        .consentTypeCd(request.getConsentTypeCd())
                        .signedYn(signedYn)
                        .build();
        return toDto(consentRepository.save(consent));
    }

    /**
     * 엔티티 → DTO 변환
     *
     * <p>엔티티를 그대로 응답에 쓰지 않는 이유 — 엔티티는 DB 구조를 그대로 드러내고
     * JPA 영속성 컨텍스트에 묶여 있다. DTO로 한 겹 끊어야 테이블이 바뀌어도 API 계약이
     * 흔들리지 않는다. 필드명은 프론트 types.ts 의 Consent 와 1:1로 맞춘다.</p>
     */
    private ConsentDto toDto(Consent c) {
        return new ConsentDto(
                c.getConsentId(),
                c.getSurgeryId(),
                c.getAuthorStaffId(),
                c.getConsentTypeCd(),
                c.getSignedYn(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
