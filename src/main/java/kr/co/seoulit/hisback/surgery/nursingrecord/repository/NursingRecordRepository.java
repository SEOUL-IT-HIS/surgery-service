package kr.co.seoulit.hisback.surgery.nursingrecord.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.nursingrecord.entity.NursingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술간호기록 JPA 리포지토리
 */
public interface NursingRecordRepository extends JpaRepository<NursingRecord, String> {
    List<NursingRecord> findBySurgeryId(String surgeryId);
}
