package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 수술실 JPA 리포지토리
 */
public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, Long> {

    /** 사용 중(active)인 수술실 목록 (SL2-6) */
    List<OperatingRoom> findByActiveTrue();

    /** 수술방 코드 중복 검사 */
    boolean existsByRoomCode(String roomCode);
}
