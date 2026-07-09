package kr.co.seoulit.hisback.surgery.anesthesia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 마취/수술 동의서 확인기록 엔티티 (SL2-38 / FR-SUR-006)
 * <p>데이터모델 7절 AnesthesiaConsent 참조. Sign In 체크리스트(SL2-46)의 동의서 확인 항목과 연계된다.</p>
 */
@Entity
@Table(name = "anesthesia_consent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaConsent {

    /** 동의서 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consent_id")
    private Long consentId;

    /** 원 수술 ID */
    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

    /** 동의서 종류 (SURGERY: 수술 / ANESTHESIA: 마취) */
    @Column(name = "consent_type", length = 20)
    private String consentType;

    /** 서명자 성명 */
    @Column(name = "signer_name", length = 50)
    private String signerName;

    /** 서명자와 환자의 관계 (본인/법정대리인 등) */
    @Column(name = "signer_relation", length = 20)
    private String signerRelation;

    /** 설명 의사 ID/성명 */
    @Column(name = "explained_by", length = 50)
    private String explainedBy;

    /** 동의 서명 일시 */
    @Column(name = "signed_dt")
    private LocalDateTime signedDt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
