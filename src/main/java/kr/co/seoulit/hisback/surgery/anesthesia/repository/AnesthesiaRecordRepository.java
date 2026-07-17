package kr.co.seoulit.hisback.surgery.anesthesia.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 마취기록 JPA 리포지토리
 */
public interface AnesthesiaRecordRepository extends JpaRepository<AnesthesiaRecord, String> {
    List<AnesthesiaRecord> findBySurgeryId(String surgeryId);
}
