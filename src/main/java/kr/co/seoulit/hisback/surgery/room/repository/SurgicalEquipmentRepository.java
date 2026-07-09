package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 수술장비 JPA 리포지토리
 */
public interface SurgicalEquipmentRepository extends JpaRepository<SurgicalEquipment, Long> {

    /** 사용 중(active)인 장비 목록 (SL2-9) */
    List<SurgicalEquipment> findByActiveTrue();

    /** 장비 코드 중복 검사 */
    boolean existsByEquipmentCode(String equipmentCode);
}
