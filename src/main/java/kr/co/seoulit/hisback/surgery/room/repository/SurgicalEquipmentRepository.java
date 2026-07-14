package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술장비 JPA 리포지토리
 */
public interface SurgicalEquipmentRepository extends JpaRepository<SurgicalEquipment, String> {
}
