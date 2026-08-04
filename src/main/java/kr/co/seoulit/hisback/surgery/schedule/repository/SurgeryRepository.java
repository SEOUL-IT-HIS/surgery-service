package kr.co.seoulit.hisback.surgery.schedule.repository;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술(스케줄) JPA 리포지토리
 */
public interface SurgeryRepository extends JpaRepository<Surgery, String> {
    List<Surgery> findBySurgeryDt(LocalDate surgeryDt);

    /**
     * 배정 대기 목록 — 응급('Y')이 먼저, 같은 등급이면 희망일이 빠른 순.
     *
     * <p>emergency_yn 이 CHAR(1) 이라 내림차순 정렬하면 'Y' 가 'N' 보다 앞선다(§14.2).
     * 프론트 요청 대기 화면이 이 순서를 그대로 그리므로 화면에서 다시 정렬하지 않는다.</p>
     */
    List<Surgery> findByStatusCdOrderByEmergencyYnDescSurgeryDtAsc(String statusCd);
}
