package kr.co.seoulit.hisback.surgery.room.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 수술실 JPA 리포지토리
 * <p>JpaRepository&lt;OperatingRoom, String&gt;만 상속하면 findAll/findById/save 등 기본 CRUD는
 * Spring Data JPA가 자동 구현한다. 대상 테이블은 엔티티(OperatingRoom)의
 * {@code @Table(name = "SURGERY_ROOM")} 매핑을 그대로 따라간다.</p>
 */
public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, String> {

    /**
     * 상태코드로 수술실을 조회한다. 메서드 이름의 {@code StatusCd}는 엔티티 필드명(statusCd)이며,
     * Spring Data JPA가 {@code WHERE status_cd = ?} SQL을 자동 생성한다.
     * (어떤 상태를 '사용가능'으로 볼지는 서비스 계층에서 결정 — OR_STATUS_CD 01=사용가능)
     */
    List<OperatingRoom> findByStatusCd(String statusCd);
}
