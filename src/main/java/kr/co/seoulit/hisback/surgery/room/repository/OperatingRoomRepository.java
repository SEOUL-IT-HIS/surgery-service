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
// ─────────────────────────────────────────────────────────────────────────────
// 이 파일은 리포지토리 계층의 '대표 예제'다. 다른 리포지토리도 같은 모양이다.
//
// 어노테이션이 하나도 없는 점에 주목할 것. @Repository 를 붙이지 않아도 되는 이유는
// JpaRepository 를 상속한 인터페이스를 Spring Data 가 알아서 찾아 구현체를 만들어
// 빈으로 등록하기 때문이다. 붙여도 동작하지만 중복이다.
//
// 구현 클래스를 우리가 작성하지 않는다는 점도 중요하다. 인터페이스만 선언하면
// Spring Data 가 실행 시점에 구현체를 만들어 끼워 넣는다.
// ─────────────────────────────────────────────────────────────────────────────

// JpaRepository<엔티티, PK타입>
//   - 첫 번째: 다룰 엔티티 (OperatingRoom)
//   - 두 번째: 그 엔티티 @Id 필드의 타입 (roomCode 가 String 이므로 String)
//     PK 가 숫자면 Long 을 쓴다. 여기를 틀리면 기동 시점에 에러가 난다.
//
// 상속만으로 아래가 공짜로 생긴다.
//   save(entity) · findById(id) · findAll() · findAll(Pageable) · delete(entity) · count() ...
public interface OperatingRoomRepository extends JpaRepository<OperatingRoom, String> {

    /**
     * 상태코드로 수술실을 조회한다.
     *
     * <p><b>쿼리 메서드</b> — SQL 을 한 줄도 쓰지 않았는데 동작하는 이유는,
     * Spring Data 가 <b>메서드 이름을 해석해</b> 쿼리를 만들어주기 때문이다.</p>
     *
     * <pre>
     *   findBy  StatusCd          →  SELECT * FROM SURGERY_ROOM WHERE status_cd = ?
     *   ~~~~~~  ~~~~~~~~
     *   조회     엔티티 필드명(statusCd)
     * </pre>
     *
     * <p>이름 규칙이라 <b>오타가 나면 기동 시점에 에러</b>가 난다 — 실행 중에 조용히
     * 틀리는 게 아니라 뜨자마자 알 수 있어 오히려 안전하다.
     * 다만 필드명을 바꾸면 이 메서드 이름도 같이 바꿔야 한다.</p>
     *
     * <p>And/Or/OrderBy/Containing 등을 이어 붙일 수 있다. 예를 들어
     * {@code findByStatusCdOrderByRoomNameAsc} 처럼 쓴다. 다만 이름이 길어져
     * 읽기 어려워지면 {@code @Query} 로 JPQL 을 직접 쓰는 편이 낫다
     * (ConsentRepository.findByPatientId 가 그 예다).</p>
     *
     * <p>어떤 상태를 '사용가능'으로 볼지는 여기서 정하지 않는다. 리포지토리는 조회 수단만
     * 제공하고, 01 이 사용가능이라는 <b>업무 규칙은 서비스 계층</b>이 갖는다.</p>
     */
    List<OperatingRoom> findByStatusCd(String statusCd);
}
