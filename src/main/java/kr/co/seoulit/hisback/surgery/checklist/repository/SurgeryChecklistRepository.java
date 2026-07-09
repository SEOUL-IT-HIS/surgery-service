package kr.co.seoulit.hisback.surgery.checklist.repository;

import kr.co.seoulit.hisback.surgery.checklist.entity.ChecklistPhase;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 수술안전체크리스트 JPA 리포지토리
 */
public interface SurgeryChecklistRepository extends JpaRepository<SurgeryChecklist, Long> {

    /** 특정 수술의 체크리스트 전체 (SL2-35) */
    List<SurgeryChecklist> findBySurgeryIdOrderByPhaseAsc(Long surgeryId);

    /** 특정 수술의 특정 단계 체크리스트 */
    Optional<SurgeryChecklist> findBySurgeryIdAndPhase(Long surgeryId, ChecklistPhase phase);
}
