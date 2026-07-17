package kr.co.seoulit.hisback.surgery.surgeryrecord.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.surgeryrecord.entity.OperativeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술기록 JPA 리포지토리
 */
public interface OperativeRecordRepository extends JpaRepository<OperativeRecord, String> {
    List<OperativeRecord> findBySurgeryId(String surgeryId);
}
