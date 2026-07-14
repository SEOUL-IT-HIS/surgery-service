package kr.co.seoulit.hisback.surgery.schedule.repository;

import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수술(스케줄) JPA 리포지토리
 */
public interface SurgeryRepository extends JpaRepository<Surgery, String> {

    /** 예정일시 구간으로 조회 (금일 현황/일자별 조회) */
    List<Surgery> findByScheduledAtBetweenOrderByScheduledAtAsc(LocalDateTime start, LocalDateTime end);

    /** 특정 수술실의 예정일시 구간 조회 (일정 충돌 검사) */
    List<Surgery> findByRoomCodeAndScheduledAtBetween(String roomCode,
                                                        LocalDateTime start,
                                                        LocalDateTime end);

    /** 상태별 조회 */
    List<Surgery> findByStatus(SurgeryStatus status);
}
