package kr.co.seoulit.hisback.surgery.consent.service;

import java.util.List;
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

    @Override
    public ConsentDto createConsent(ConsentDto request) {
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
