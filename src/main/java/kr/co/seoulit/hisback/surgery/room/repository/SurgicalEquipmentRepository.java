package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 수술장비 JPA 리포지토리
 * <p>JpaRepository&lt;엔티티, PK타입&gt; 상속만으로 findAll/findById/save/delete 등 기본 CRUD를
 * Spring Data JPA가 자동 구현한다. 대상 테이블은 엔티티(SurgicalEquipment)의
 * {@code @Table(name = "SURGERY_EQUIPMENT")} 매핑을 그대로 따라간다.</p>
 */
public interface SurgicalEquipmentRepository extends JpaRepository<SurgicalEquipment, String> {
    List<SurgicalEquipment> findByStatusCd(String statusCd);
}
