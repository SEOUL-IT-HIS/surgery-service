package kr.co.seoulit.hisback.surgery.checklist.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술안전체크리스트 JPA 리포지토리
 */
public interface SurgeryChecklistRepository extends JpaRepository<SurgeryChecklist, String> {
    List<SurgeryChecklist> findBySurgeryId(String surgeryId);

    /**
     * 그 단계를 <b>완료했는지</b>.
     *
     * <p>수술 완료 전에 Sign Out 이 끝났는지 확인할 때 쓴다(SurgeryScheduleServiceImpl).
     * 행이 있는지만 보면 안 된다 — 작성 시작만 해 두면 completedYn 이 'N' 인 행이
     * 생기기 때문이다.</p>
     */
    boolean existsBySurgeryIdAndPhaseCdAndCompletedYn(
            String surgeryId, String phaseCd, String completedYn);
}
