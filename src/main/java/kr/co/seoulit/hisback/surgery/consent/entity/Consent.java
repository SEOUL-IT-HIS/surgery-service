package kr.co.seoulit.hisback.surgery.consent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 동의서 엔티티 (구 ANESTHESIA_CONSENT에서 분리된 CONSENT 테이블)
 * <p>전자문서 원본은 보관하지 않고(§21.5, 부서 비치 종이 양식 사용), 동의 여부/일시/서명자만
 * 관리한다. signed_by는 그 화면에서 직접 입력·확정되는 원본 데이터라 §14.1 스냅샷 금지
 * 규칙의 예외로 그대로 저장한다.</p>
 */
@Entity
@Table(name = "CONSENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @Id
    @Column(name = "consent_id", length = 36, nullable = false)
    private String consentId;

    // FK -> SURGERY
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // 논리참조(Staff/Provider), 물리FK 아님
    @Column(name = "author_staff_id_fk", length = 36)
    private String authorStaffId;

    // SURG_CONSENT_CD: 01수술/02마취/03비용견적
    @Column(name = "consent_type_cd", length = 20)
    private String consentTypeCd;

    // SIGNER_RELATION_CD: 01본인/02법정대리인/03배우자/04기타
    @Column(name = "signer_relation_cd", length = 20)
    private String signerRelationCd;

    // §14.1 스냅샷 금지 예외: 그 화면에서 직접 입력·확정되는 원본 데이터
    @Column(name = "signed_by", length = 50)
    private String signedBy;

    @Column(name = "signed_dt")
    private LocalDate signedDt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
