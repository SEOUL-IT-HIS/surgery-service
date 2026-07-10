package kr.co.seoulit.hisback.surgery.consent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 마취/수술 동의서 확인기록 엔티티 (SL2-38 / SL2-53 / FR-SUR-006)
 * <p>데이터모델 7절 AnesthesiaConsent 참조. Sign In 체크리스트(SL2-46)의 동의서 확인 항목과 연계된다.</p>
 * <p>컴포넌트 분리(SL2-42 consent)로 anesthesia 패키지에서 이동됨.</p>
 * <p>PK 채번·테이블/컬럼 식별자는 프로젝트 공통 규약(오라클 시퀀스 + 대문자 식별자; STAFF/EMP_PHOTO 참조)에 맞춘다.</p>
 */
@Entity
@Table(name = "ANESTHESIA_CONSENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaConsent {

    /** 동의서 고유번호 (PK: 오라클 시퀀스 ANESTHESIA_CONSENT_SEQ로 자동 채번) */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anesthesiaConsentSeq")
    @SequenceGenerator(name = "anesthesiaConsentSeq", sequenceName = "ANESTHESIA_CONSENT_SEQ", allocationSize = 1)
    @Column(name = "CONSENT_ID")
    private Long consentId;

    /** 원 수술 ID */
    @Column(name = "SURGERY_ID", nullable = false)
    private Long surgeryId;

    /** 동의서 종류 (SURGERY: 수술 / ANESTHESIA: 마취) — 필수값 */
    @Column(name = "CONSENT_TYPE", nullable = false, length = 20)
    private String consentType;

    /** 서명자 성명 — 동의서 필수값(서명자 없는 동의 기록 방지) */
    @Column(name = "SIGNER_NAME", nullable = false, length = 50)
    private String signerName;

    /** 서명자와 환자의 관계 (본인/법정대리인 등) */
    @Column(name = "SIGNER_RELATION", length = 20)
    private String signerRelation;

    /** 설명 의사 ID/성명 */
    @Column(name = "EXPLAINED_BY", length = 50)
    private String explainedBy;

    /** 동의 서명 일시 */
    @Column(name = "SIGNED_DT")
    private LocalDateTime signedDt;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
