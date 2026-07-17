package kr.co.seoulit.hisback.surgery.checklist.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술안전체크리스트 JPA 리포지토리
 */
public interface SurgeryChecklistRepository extends JpaRepository<SurgeryChecklist, String> {
    List<SurgeryChecklist> findBySurgeryId(String surgeryId);
}
