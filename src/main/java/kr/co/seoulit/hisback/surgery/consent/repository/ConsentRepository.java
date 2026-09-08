package kr.co.seoulit.hisback.surgery.consent.repository;

import java.util.List;
import java.util.Optional;
import kr.co.seoulit.hisback.surgery.consent.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 동의서 JPA 리포지토리
 */
public interface ConsentRepository extends JpaRepository<Consent, String> {

    List<Consent> findBySurgeryId(String surgeryId);

    /**
     * (수술, 동의종류) 조합의 행 하나.
     *
     * <p>수술:동의서는 1:N 이지만 (수술, 동의종류) 조합은 1건이어야 한다. 체크/해제가
     * 같은 행을 오가므로 존재 여부(exists)가 아니라 행 자체가 필요하다.</p>
     */
    Optional<Consent> findBySurgeryIdAndConsentTypeCd(String surgeryId, String consentTypeCd);

    /**
     * SL2-217: 그 종류의 동의서를 <b>실제로 받았는지</b>.
     *
     * <p>행이 있는지만 묻던 것을 {@code signedYn='Y'} 까지 확인하도록 바꿨다.
     * 체크를 해제하면 행은 남고 값만 N 이 되므로, 존재 여부만 보면 해제한 동의서도
     * 받은 것으로 통과한다.</p>
     */
    boolean existsBySurgeryIdAndConsentTypeCdAndSignedYn(
            String surgeryId, String consentTypeCd, String signedYn);

    /**
     * SL2-222: 환자별 동의서 이력 조회.
     *
     * <p>CONSENT 에는 patient_id 가 없어 SURGERY 를 거쳐야 한다. 두 테이블 모두
     * surgery-service 소유라 서비스 내부 조인이며 §21.2(타 서비스 DB 직접 조회 금지)와
     * 무관하다. 최신 순으로 내려 이력으로 읽히게 한다.</p>
     *
     * <p>정렬 기준이 signedDt 에서 createdAt 으로 바뀌었다 — 서명일 컬럼을 없애면서
     * (2026-09-03) 시간 정보가 생성 시각뿐이 됐다.</p>
     */
    @Query("""
            select c from Consent c
            where c.surgeryId in (
                select s.surgeryId from Surgery s where s.patientId = :patientId
            )
            order by c.createdAt desc
            """)
    List<Consent> findByPatientId(@Param("patientId") String patientId);
}
