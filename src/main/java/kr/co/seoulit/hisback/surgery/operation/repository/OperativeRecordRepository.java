package kr.co.seoulit.hisback.surgery.operation.repository;

import kr.co.seoulit.hisback.surgery.operation.entity.OperativeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 수술기록지 JPA 리포지토리
 */
public interface OperativeRecordRepository extends JpaRepository<OperativeRecord, Long> {

    /** 특정 수술의 수술기록지 목록 (SL2-57) */
    List<OperativeRecord> findBySurgeryIdOrderByCreatedAtAsc(Long surgeryId);
}
