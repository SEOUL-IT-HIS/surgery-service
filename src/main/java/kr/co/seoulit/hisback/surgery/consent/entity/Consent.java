package kr.co.seoulit.hisback.surgery.consent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
 * <p>전자문서 원본은 보관하지 않는다(§21.5, 부서 비치 종이 양식 사용). 시스템이 남기는
 * 것은 <b>그 종이를 받았는가</b> 하나뿐이다.</p>
 *
 * <p><b>서명자·서명일은 2026-09-03 제거했다.</b> 종이에 이미 적혀 있는 값을 화면에서
 * 다시 타이핑하게 하고 있었다 — 입력만 늘고 틀릴 여지도 생겼다. 확인 절차가
 * 체크리스트처럼 체크 한 번으로 끝나도록 컬럼도 플래그 하나로 줄였다.
 * 언제 체크했는지는 created_at·updated_at 에 남는다.</p>
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

    @Column(name = "author_staff_id", length = 36)
    private String authorStaffId;

    // 동의 종류: 01수술/02마취/03비용견적.
    // 코드값은 수술 기준 숫자로 확정했다. 다만 admin 의 CONSENT_TYPE_CD 그룹은
    // 검사·영상이 CONTRAST·INVASIVE 처럼 영문으로 함께 쓰고 있어 한 그룹에 두 체계가 섞인다.
    // 영문 두 개를 숫자로 바꿀지 그대로 둘지는 그쪽 담당자와 정해야 한다.
    @Column(name = "consent_type_cd", length = 36)
    private String consentTypeCd;

    // 동의서 수령 여부. 프로젝트 표준 Y/N 플래그(§14.2)
    //
    // 체크를 되돌릴 수 있어야 해서 행을 지우지 않고 N 으로 둔다 — 지우면
    // "받은 적 없음"과 "받았다가 취소함"이 구분되지 않는다(§21.6).
    @Column(name = "signed_yn", length = 1, nullable = false)
    private String signedYn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
