package kr.co.seoulit.hisback.surgery.anesthesia.repository;

import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 마취기록 JPA 리포지토리
 */
public interface AnesthesiaRecordRepository extends JpaRepository<AnesthesiaRecord, Long> {

    /** 특정 수술의 마취기록 목록 (SL2-34) */
    List<AnesthesiaRecord> findBySurgeryIdOrderByCreatedAtAsc(Long surgeryId);
}
