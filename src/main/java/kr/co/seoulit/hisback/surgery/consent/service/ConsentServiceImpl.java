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
 * 동의서 관리 서비스 구현체
 */
@Service
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;

    public ConsentServiceImpl(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

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
