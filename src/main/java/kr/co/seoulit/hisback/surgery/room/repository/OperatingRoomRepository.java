package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술실 JPA 리포지토리
 */
public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, String> {
}
