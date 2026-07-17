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
}
