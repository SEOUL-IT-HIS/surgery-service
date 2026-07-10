package kr.co.seoulit.hisback.surgery.consent.dto;

import kr.co.seoulit.hisback.surgery.consent.entity.AnesthesiaConsent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 마취/수술 동의서 DTO (SL2-38 / SL2-53 / SL2-54 / API-SUR-004)
 * 컴포넌트 분리(SL2-42 consent)로 anesthesia 패키지에서 이동됨.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaConsentDto {

    private Long consentId;
    private Long surgeryId;
    private String consentType;
    private String signerName;
    private String signerRelation;
    private String explainedBy;
    private LocalDateTime signedDt;
    private LocalDateTime createdAt;

    public static AnesthesiaConsentDto from(AnesthesiaConsent c) {
        return AnesthesiaConsentDto.builder()
                .consentId(c.getConsentId())
                .surgeryId(c.getSurgeryId())
                .consentType(c.getConsentType())
                .signerName(c.getSignerName())
                .signerRelation(c.getSignerRelation())
                .explainedBy(c.getExplainedBy())
                .signedDt(c.getSignedDt())
                .createdAt(c.getCreatedAt())
                .build();
    }

    public AnesthesiaConsent toEntity(Long surgeryId) {
        return AnesthesiaConsent.builder()
                .surgeryId(surgeryId)
                .consentType(consentType != null ? consentType : "SURGERY")
                .signerName(signerName)
                .signerRelation(signerRelation)
                .explainedBy(explainedBy)
                .signedDt(signedDt)
                .build();
    }
}
