package kr.co.seoulit.hisback.surgery.surgeryorder.repository;

import java.time.LocalDate;
import kr.co.seoulit.hisback.surgery.surgeryorder.entity.SurgeryOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 수술 오더 JPA 리포지토리
 *
 * <p>PK 인 {@code orderId} 가 VARCHAR2(36) 문자열이라 두 번째 타입 인자가 String 이다(§14.2).</p>
 */
public interface SurgeryOrderRepository extends JpaRepository<SurgeryOrder, String> {

    /**
     * 오더 목록 조회 — 상태·응급여부·환자·기간으로 거른다.
     *
     * <p><b>메서드 이름 규칙 대신 {@code @Query} 를 쓴 이유</b> — 조건이 모두 선택이라
     * 이름 규칙으로는 조합마다 메서드를 따로 만들어야 한다. 조건 4개면 16가지다.
     * {@code (:param is null or 컬럼 = :param)} 형태는 값이 없을 때 조건이 항상 참이 되어
     * 없는 것과 같아진다.</p>
     *
     * <p>정렬은 {@link Pageable} 에 맡긴다. 기본값(응급 우선)은 컨트롤러가 정한다 —
     * 배정 담당자가 먼저 처리해야 할 것이 응급이다.</p>
     *
     * @param orderStatusCd 오더 상태. null 이면 전체(반려된 것도 나온다)
     * @param emergencyYn 'Y'/'N'. null 이면 전체
     * @param patientId 환자 식별자. null 이면 전체
     * @param fromDt 희망일 시작. null 이면 하한 없음
     * @param toDt 희망일 종료. null 이면 상한 없음
     */
    @Query(
            "select o from SurgeryOrder o "
                    + "where (:orderStatusCd is null or o.orderStatusCd = :orderStatusCd) "
                    + "and (:emergencyYn is null or o.emergencyYn = :emergencyYn) "
                    + "and (:patientId is null or o.patientId = :patientId) "
                    + "and (:fromDt is null or o.requestedDt >= :fromDt) "
                    + "and (:toDt is null or o.requestedDt <= :toDt)")
    Page<SurgeryOrder> search(
            @Param("orderStatusCd") String orderStatusCd,
            @Param("emergencyYn") String emergencyYn,
            @Param("patientId") String patientId,
            @Param("fromDt") LocalDate fromDt,
            @Param("toDt") LocalDate toDt,
            Pageable pageable);

    /**
     * 수술 식별자로 오더를 되짚는다.
     *
     * <p>수술 화면에서 "이 수술이 어느 요청에서 나왔는지"를 보여줄 때 쓴다. 수락된 오더만
     * {@code surgeryId} 를 갖고 있으므로, 없으면 비어 있는 결과가 정상이다.</p>
     */
    java.util.Optional<SurgeryOrder> findBySurgeryId(String surgeryId);
}
