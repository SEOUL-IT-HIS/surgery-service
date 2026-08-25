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

    // 배정 대기 목록 조회는 오더로 옮겼다 — SurgeryOrderRepository.search
    //   요청접수(00) 상태의 수술이 더는 생기지 않아 상태 고정 조회가 필요 없어졌다.

    /**
     * SL2-170: 배정 현황 조회 — 기간·수술실·상태로 거른다.
     *
     * <p>오더 쪽 {@code SurgeryOrderRepository.search} 와 성격이 다르다 — 그쪽은 아직
     * 수술이 되지 않은 <b>요청</b>을 상태·응급여부로 거르고, 이쪽은 이미 만들어진
     * <b>수술</b>을 수술실·기간으로 거른다. 한 메서드에 조건을 다 몰면 파라미터가
     * 일곱 개가 되고, 어느 조합이 어느 화면용인지 읽어서는 알 수 없게 된다.</p>
     *
     * <p><b>수술실 미배정 건도 포함된다</b>({@code roomCode} 를 안 주면). 배정 현황을 보는
     * 목적 중 하나가 "아직 방이 안 잡힌 건"을 찾는 것이라, 빼면 그게 안 보인다.</p>
     *
     * <p>취소(04)는 기본적으로 섞인다 — 상태를 지정하지 않으면 전부 나온다. 취소를 빼고
     * 보려면 {@code statusCd} 를 주면 된다. 여기서 임의로 제외하지 않는 이유는
     * "그날 그 방에 무엇이 있었나"에 취소도 사실이기 때문이다.</p>
     *
     * <p><b>환자·집도의 조건 추가</b>(2026-08-25, SL2-314·334) — 수술 기록지와 간호기록
     * 조회 화면이 "환자·수술일자·수술실·집도의"로 찾기를 요구한다. 앞의 둘만 있어서
     * 화면이 전건을 받아 스스로 거르는 수밖에 없었는데, 그러면 건수가 늘 때 목록 전체를
     * 내려받게 되고 페이징도 의미가 없어진다.</p>
     *
     * <p>환자·집도의는 다른 서비스가 소유한 식별자라 <b>정확히 일치</b>로만 찾는다(§21.9).
     * 이름으로 찾으려면 그쪽 서비스에서 식별자를 받아와야 한다 — 우리가 이름을 갖고
     * 있지 않으므로 like 검색은 애초에 불가능하다.</p>
     */
    @Query(
            "select s from Surgery s "
                    + "where (:roomCode is null or s.roomCode = :roomCode) "
                    + "and (:statusCd is null or s.statusCd = :statusCd) "
                    + "and (:patientId is null or s.patientId = :patientId) "
                    + "and (:surgeonId is null or s.surgeonId = :surgeonId) "
                    + "and (:fromDt is null or s.surgeryDt >= :fromDt) "
                    + "and (:toDt is null or s.surgeryDt <= :toDt)")
    Page<Surgery> searchAssignments(
            @Param("roomCode") String roomCode,
            @Param("statusCd") String statusCd,
            @Param("patientId") String patientId,
            @Param("surgeonId") String surgeonId,
            @Param("fromDt") LocalDate fromDt,
            @Param("toDt") LocalDate toDt,
            Pageable pageable);
}
