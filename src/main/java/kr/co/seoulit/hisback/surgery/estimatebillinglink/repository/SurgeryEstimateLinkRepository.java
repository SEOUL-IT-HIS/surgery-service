package kr.co.seoulit.hisback.surgery.estimatebillinglink.repository;

import kr.co.seoulit.hisback.surgery.estimatebillinglink.entity.SurgeryEstimateLink;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술 견적 연계 상태 JPA 리포지토리 (SL2-67 조회 / SL2-68 상태변경)
 *
 * <p>JpaRepository&lt;엔티티, PK타입&gt; — 두 번째 인자는 엔티티 {@code @Id} 필드의 타입이다.
 * SurgeryEstimateLink 는 수술 1건에 연계 상태 1건인 1:1 구조라 surgery_id 가 곧 PK 이고,
 * 타입은 VARCHAR2(36) 문자열이므로 String 이다(§14.2).</p>
 *
 * <p>조회 메서드를 따로 두지 않은 이유 — PK 가 surgery_id 라서 기본 제공되는
 * {@code findById(surgeryId)} 로 충분하다.</p>
 */
public interface SurgeryEstimateLinkRepository
        extends JpaRepository<SurgeryEstimateLink, String> {
}
