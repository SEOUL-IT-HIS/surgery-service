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
 *
 * <p>전자문서 원본은 보관하지 않고(§21.5, 부서 비치 종이 양식 사용), 동의 여부/일시/서명자만
 * 관리한다. signed_by는 그 화면에서 직접 입력·확정되는 원본 데이터라 §14.1 스냅샷 금지
 * 규칙의 예외로 그대로 저장한다.</p>
 *
 * <p><b>서명자 관계(signer_relation_cd)는 2026-08-10 제거했다.</b> 프로젝트 범위를
 * "동의 여부 확인"으로 축소하기로 정해졌고, admin 에서도 RELATION_CD 코드그룹이 함께
 * 내려갔다. 본인/법정대리인 구분은 종이 동의서에서 관리한다.</p>
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

    // 논리참조(Staff/Provider), 물리FK 아님 — 직원 서비스가 원본을 소유하므로 식별자만 갖는다(§21.9)
    //
    // 컬럼명을 author_staff_id_fk 에서 바꿨다(2026-08-10). §14.1 의 FK 규칙은
    // {참조테이블명}_id 이고 §14.2 접미사 표에도 _fk 는 없다. 프로젝트 전체에서 _fk 가 붙은
    // 컬럼은 이 하나뿐이라 표준형으로 되돌린 것이다.
    // 물리 FK 를 걸지 않는다는 사실은 컬럼명이 아니라 위 주석으로 남긴다 — 이름에 담으면
    // 규칙에서 벗어나고, JSON 키(authorStaffId)와도 어긋난다.
    //
    // ddl-auto=update 는 컬럼 이름을 바꿔주지 않는다. 새 이름의 빈 컬럼을 하나 더 만들 뿐이라
    // 기존 값이 남겨진다. 배포 전에 Oracle 에서 먼저 이름을 바꿔야 한다:
    //   ALTER TABLE CONSENT RENAME COLUMN author_staff_id_fk TO author_staff_id;
    @Column(name = "author_staff_id", length = 36)
    private String authorStaffId;

    // 동의 종류: 01수술/02마취/03비용견적.
    // TODO: admin 의 CONSENT_TYPE_CD(57)와 코드값 체계가 다르다(영문 vs 숫자). 협의 후 통일.
    @Column(name = "consent_type_cd", length = 20)
    private String consentTypeCd;

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
