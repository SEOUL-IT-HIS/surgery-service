package kr.co.seoulit.hisback.surgery.consent.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.consent.dto.ConsentDto;
import kr.co.seoulit.hisback.surgery.consent.entity.Consent;
import kr.co.seoulit.hisback.surgery.consent.repository.ConsentRepository;
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

    private final ConsentRepository consentRepository;

    // 생성자가 하나뿐이라 @Autowired 없이도 Spring이 의존성을 주입한다
    public ConsentServiceImpl(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    /**
     * SL2-54: 특정 수술의 동의서 목록
     *
     * <p>수술 존재 여부를 확인하지 않는 이유 — 없는 수술이면 빈 목록이 나올 뿐이고,
     * 조회에서 404를 던지려면 schedule 쪽 조회를 한 번 더 해야 한다. 읽기 요청에
     * 불필요한 결합을 만들지 않는다.</p>
     */
    @Override
    public List<ConsentDto> getConsents(String surgeryId) {
        return consentRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** SL2-222: 환자별 동의서 이력 — 환자 존재 여부는 patient-service 소관이라 여기서 확인하지 않는다(§21.1). */
    @Override
    public List<ConsentDto> getConsentsByPatient(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("환자 식별자는 필수입니다");
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
                        .orElseThrow(() -> new NoSuchElementException("동의서를 찾을 수 없습니다: " + consentId)));
    }

    /**
     * SL2-53: 동의 확인 기록
     *
     * <p>같은 수술에 같은 종류의 동의서를 두 번 남기지 않는다. 재동의는 기존 행 수정이 아니라
     * 업무 재정의 대상이라 여기서 막는다(§21.6 이력 보존).</p>
     */
    @Override
    public ConsentDto createConsent(ConsentDto request) {
        if (consentRepository.existsBySurgeryIdAndConsentTypeCd(
                request.getSurgeryId(), request.getConsentTypeCd())) {
            throw new IllegalArgumentException("이미 등록된 동의 종류입니다: " + request.getConsentTypeCd());
        }
        // PK는 서버가 채번한다. 프론트가 보낸 값이 있으면 존중하되(재시도·마이그레이션 대비),
        // 없으면 UUID로 생성한다 — surgery_id 처럼 업무 의미가 있는 코드가 아니라 내부 식별자다(§14.2)
        String consentId = request.getConsentId() != null ? request.getConsentId() : UUID.randomUUID().toString();
        Consent consent =
                Consent.builder()
                        .consentId(consentId)
                        .surgeryId(request.getSurgeryId())
                        .authorStaffId(request.getAuthorStaffId())
                        .consentTypeCd(request.getConsentTypeCd())
                        .signerRelationCd(request.getSignerRelationCd())
                        .signedBy(request.getSignedBy())
                        .signedDt(request.getSignedDt())
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
                c.getSignerRelationCd(),
                c.getSignedBy(),
                c.getSignedDt(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
