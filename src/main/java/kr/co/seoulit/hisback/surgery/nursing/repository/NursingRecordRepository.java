package kr.co.seoulit.hisback.surgery.nursing.repository;

import kr.co.seoulit.hisback.surgery.nursing.entity.NursingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 수술간호기록 JPA 리포지토리
 */
public interface NursingRecordRepository extends JpaRepository<NursingRecord, Long> {

    /** 특정 수술의 간호기록 목록 (SL2-61) */
    List<NursingRecord> findBySurgeryIdOrderByCreatedAtAsc(Long surgeryId);

    /** 특정 수술에 물품 카운트 불일치(미해결) 기록이 존재하는지 (BR-013) */
    boolean existsBySurgeryIdAndCountFinalIsNotNullAndCountMatchedFalse(Long surgeryId);
}
