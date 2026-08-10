package kr.co.seoulit.hisback.surgery.estimatebillinglink.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.estimatebillinglink.entity.SurgeryPlannedItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술 예정 자원목록 JPA 리포지토리 (SL2-65 등록 / SL2-66 조회)
 *
 * <p>JpaRepository&lt;엔티티, PK타입&gt; — 두 번째 인자는 엔티티 {@code @Id} 필드의 타입이다.
 * SurgeryPlannedItem.plannedItemId 가 VARCHAR2(36) 문자열이므로 String 이다(§14.2).</p>
 */
public interface SurgeryPlannedItemRepository
        extends JpaRepository<SurgeryPlannedItem, String> {

    /**
     * 특정 수술의 예정 자원 목록을 조회한다.
     *
     * <p>메서드 이름의 {@code SurgeryId} 는 엔티티 필드명이며, Spring Data 가
     * {@code WHERE surgery_id = ?} SQL 을 자동 생성한다.</p>
     */
    List<SurgeryPlannedItem> findBySurgeryId(String surgeryId);
}
