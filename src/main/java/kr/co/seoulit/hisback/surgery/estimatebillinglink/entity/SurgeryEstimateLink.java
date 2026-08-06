package kr.co.seoulit.hisback.surgery.estimatebillinglink.entity;

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
 * 수술 견적 연계 상태 엔티티 (SL2-67 조회 / SL2-68 상태변경)
 *
 * <p>수납(Billing)과의 연계가 어디까지 진행됐는지만 기록한다.
 * <b>가격 정보는 갖지 않는다</b> — 견적 금액은 수납이 소유한다(§21.2).
 * 수술은 "견적을 요청했는지 / 회신을 받았는지"라는 상태만 안다.</p>
 *
 * <p>PK 가 surgery_id 인 점에 주목할 것. 수술 한 건에 연계 상태는 하나뿐이라
 * 별도 식별자를 두지 않고 수술 ID 를 그대로 기본키로 쓴다(1:1 관계).</p>
 */
@Entity
@Table(name = "SURGERY_ESTIMATE_LINK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryEstimateLink {

    // 수술 1건 : 연계상태 1건 이라 surgery_id 가 곧 PK 다.
    @Id
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // ESTIMATE_STATUS_CD: 연계 진행 상태. 코드 카탈로그는 admin-service 소관(§21.4)
    @Column(name = "estimate_status_cd", length = 36, nullable = false)
    private String estimateStatusCd;

    // 견적 요청·회신 시각. 아직 요청 전이면 null 이라 nullable 을 막지 않는다.
    @Column(name = "estimate_dt")
    private LocalDateTime estimateDt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
