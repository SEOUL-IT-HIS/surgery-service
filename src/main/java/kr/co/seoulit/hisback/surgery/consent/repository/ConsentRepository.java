package kr.co.seoulit.hisback.surgery.consent.repository;

import java.util.List;
import kr.co.seoulit.hisback.surgery.consent.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 동의서 JPA 리포지토리
 */
public interface ConsentRepository extends JpaRepository<Consent, String> {
    List<Consent> findBySurgeryId(String surgeryId);
}
