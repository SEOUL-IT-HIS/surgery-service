package kr.co.seoulit.hisback.surgery.room.repository;

import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술실 JPA 리포지토리
 * <p>JpaRepository&lt;엔티티, PK타입&gt;만 상속하면 findAll/findById/save 등 기본 CRUD는
 * Spring Data JPA가 자동 구현한다. 대상 테이블은 엔티티(OperatingRoom)의
 * {@code @Table(name = "SURGERY_ROOM")} 매핑을 그대로 따라간다.</p>
 */
public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, String> {
}
