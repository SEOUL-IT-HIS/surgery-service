package kr.co.seoulit.hisback.surgery.schedule.repository;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * SL2-235/236: 배정 대기 목록 — 검색 조건 + 페이지 단위 조회.
     *
     * <p><b>메서드 이름 규칙 대신 {@code @Query} 를 쓴 이유</b> — 조건이 선택적이라
     * {@code findByStatusCdAndEmergencyYnAndPatientId...} 로는 표현할 수 없다. 값이 없을 때는
     * 그 조건을 아예 걸지 않아야 하는데, 이름 규칙 방식은 조합마다 메서드를 따로 만들어야 한다.
     * 조건이 4개면 조합이 16가지다.</p>
     *
     * <p>{@code (:param is null or 컬럼 = :param)} 형태가 그 문제를 푼다 — 값이 null 이면
     * 조건이 항상 참이 되어 없는 것과 같아진다.</p>
     *
     * <p>정렬을 쿼리에 넣지 않고 {@link Pageable} 에 맡긴다. 화면이 정렬을 바꿀 수 있어야 하고,
     * 기본값(응급 우선)은 컨트롤러가 정한다.</p>
     *
     * @param statusCd 대상 상태. 배정 대기는 요청접수(00)
     * @param emergencyYn 'Y'/'N'. null 이면 전체
     * @param patientId 환자 식별자. null 이면 전체
     * @param fromDt 희망일 시작. null 이면 하한 없음
     * @param toDt 희망일 종료. null 이면 상한 없음
     */
    @Query(
            "select s from Surgery s "
                    + "where s.statusCd = :statusCd "
                    + "and (:emergencyYn is null or s.emergencyYn = :emergencyYn) "
                    + "and (:patientId is null or s.patientId = :patientId) "
                    + "and (:fromDt is null or s.surgeryDt >= :fromDt) "
                    + "and (:toDt is null or s.surgeryDt <= :toDt)")
    Page<Surgery> searchByStatus(
            @Param("statusCd") String statusCd,
            @Param("emergencyYn") String emergencyYn,
            @Param("patientId") String patientId,
            @Param("fromDt") LocalDate fromDt,
            @Param("toDt") LocalDate toDt,
            Pageable pageable);
}
