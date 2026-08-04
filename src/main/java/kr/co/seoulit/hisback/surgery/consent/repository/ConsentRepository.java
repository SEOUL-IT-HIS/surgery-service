package kr.co.seoulit.hisback.surgery.consent.repository;

import java.util.List;
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
     * 같은 수술에 같은 종류의 동의서가 이미 있는지 (중복 등록 차단).
     * <p>수술:동의서는 1:N 이지만 (수술, 동의종류) 조합은 1건이어야 한다.</p>
     */
    boolean existsBySurgeryIdAndConsentTypeCd(String surgeryId, String consentTypeCd);

    /**
     * SL2-222: 환자별 동의서 이력 조회.
     *
     * <p>CONSENT 에는 patient_id 가 없어 SURGERY 를 거쳐야 한다. 두 테이블 모두
     * surgery-service 소유라 서비스 내부 조인이며 §21.2(타 서비스 DB 직접 조회 금지)와
     * 무관하다. 최신 서명일부터 내려 이력으로 읽히게 한다.</p>
     */
    @Query("""
            select c from Consent c
            where c.surgeryId in (
                select s.surgeryId from Surgery s where s.patientId = :patientId
            )
            order by c.signedDt desc
            """)
    List<Consent> findByPatientId(@Param("patientId") String patientId);
}
