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

    /**
     * SL2-170: 배정 현황 조회 — 기간·수술실·상태로 거른다.
     *
     * <p>{@link #searchByStatus} 와 나눈 이유 — 그쪽은 상태가 <b>고정</b>(요청접수)이고 응급여부로
     * 거른다. 이쪽은 상태가 <b>선택</b>이고 수술실로 거른다. 한 메서드에 조건을 다 몰면
     * 파라미터가 일곱 개가 되고, 어느 조합이 어느 화면용인지 읽어서는 알 수 없게 된다.</p>
     *
     * <p><b>수술실 미배정 건도 포함된다</b>({@code roomCode} 를 안 주면). 배정 현황을 보는
     * 목적 중 하나가 "아직 방이 안 잡힌 건"을 찾는 것이라, 빼면 그게 안 보인다.</p>
     *
     * <p>취소(04)는 기본적으로 섞인다 — 상태를 지정하지 않으면 전부 나온다. 취소를 빼고
     * 보려면 {@code statusCd} 를 주면 된다. 여기서 임의로 제외하지 않는 이유는
     * "그날 그 방에 무엇이 있었나"에 취소도 사실이기 때문이다.</p>
     */
    @Query(
            "select s from Surgery s "
                    + "where (:roomCode is null or s.roomCode = :roomCode) "
                    + "and (:statusCd is null or s.statusCd = :statusCd) "
                    + "and (:fromDt is null or s.surgeryDt >= :fromDt) "
                    + "and (:toDt is null or s.surgeryDt <= :toDt)")
    Page<Surgery> searchAssignments(
            @Param("roomCode") String roomCode,
            @Param("statusCd") String statusCd,
            @Param("fromDt") LocalDate fromDt,
            @Param("toDt") LocalDate toDt,
            Pageable pageable);
}
