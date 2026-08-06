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

    // 동의 종류: 01수술/02마취/03비용견적.
    // TODO: admin 의 CONSENT_TYPE_CD(57)와 코드값 체계가 다르다(영문 vs 숫자). 협의 후 통일.
    @Column(name = "consent_type_cd", length = 20)
    private String consentTypeCd;

    // RELATION_CD(admin-service 보호자관계코드): 01배우자/02부/03모/… 11기타.
    // TODO: 동의서에는 "본인"·"법정대리인"이 필요한데 RELATION_CD 에 없다. admin 에 추가 요청 중.
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
