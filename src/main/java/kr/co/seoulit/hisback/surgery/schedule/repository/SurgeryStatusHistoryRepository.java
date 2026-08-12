package kr.co.seoulit.hisback.surgery.schedule.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술 상태변경 이력 리포지토리 (SL2-282)
 *
 * <p>JpaRepository&lt;엔티티, PK타입&gt; — PK 인 historyId 가 VARCHAR2(36) 문자열이라 String 이다(§14.2).</p>
 *
 * <p>삭제·수정 메서드를 따로 두지 않는다. JpaRepository 가 delete 를 물려주긴 하지만
 * 서비스에서 부르지 않는 것이 규칙이다 — 이력은 고치거나 지우지 않는다.</p>
 */
public interface SurgeryStatusHistoryRepository
        extends JpaRepository<SurgeryStatusHistory, String> {

    /**
     * 한 수술의 전체 이력을 시간 역순으로 조회한다.
     *
     * <p>최신이 위로 오게 하는 이유 — 화면에서 가장 먼저 보고 싶은 것은 "지금 어떻게 됐는가"다.
     * 메서드 이름의 {@code OrderByChangedAtDesc} 를 Spring Data 가 ORDER BY 로 번역한다.</p>
     */
    List<SurgeryStatusHistory> findBySurgeryIdOrderByChangedAtDesc(String surgeryId);

    /**
     * 상태 종류로 걸러 조회한다. (STATUS 만, 또는 PROGRESS 만)
     *
     * <p>한 테이블에 두 종류를 모았기 때문에 필요한 메서드다. 큰 상태 전이만 보고 싶은
     * 화면과 세부 진행단계까지 보고 싶은 화면이 다를 수 있다.</p>
     */
    List<SurgeryStatusHistory> findBySurgeryIdAndStatusTypeOrderByChangedAtDesc(
            String surgeryId, String statusType);
}
