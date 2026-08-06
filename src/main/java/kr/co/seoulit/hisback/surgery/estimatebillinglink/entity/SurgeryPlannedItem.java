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
 * 수술 예정 자원(장비·약품·재료) 목록 엔티티 (SL2-65)
 *
 * <p>수술에 쓸 예정인 품목과 수량만 기록한다. <b>가격은 갖지 않는다</b> —
 * 단가·금액은 수납(Billing) 소관이라 수술이 복제해 두면 두 곳의 값이 어긋난다(§21.2).
 * 수술은 "무엇을 몇 개 쓸 예정인지"만 알려주고, 금액 산정은 수납이 한다.</p>
 *
 * <p>품목 코드(item_code) 역시 식별자만 저장하고 품목명은 담지 않는다(§21.9).
 * 표시할 이름이 필요하면 화면이 해당 서비스에서 직접 조회한다.</p>
 */
@Entity
@Table(name = "SURGERY_PLANNED_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryPlannedItem {

    // PK 는 내부 식별자라 서버가 UUID 로 채번한다(§14.2 `_id` → VARCHAR2(36))
    @Id
    @Column(name = "planned_item_id", length = 36, nullable = false)
    private String plannedItemId;

    // FK -> SURGERY.surgery_id. 어느 수술의 예정 품목인지 나타낸다.
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // ITEM_TYPE_CD: 품목 구분(장비/약품/재료). 코드 카탈로그는 admin-service 소관(§21.4)
    @Column(name = "item_type_cd", length = 36, nullable = false)
    private String itemTypeCd;

    // 품목 식별자. 코드 체계는 품목 유형에 따라 다르며 수술은 값만 보관한다.
    @Column(name = "item_code", length = 36, nullable = false)
    private String itemCode;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
