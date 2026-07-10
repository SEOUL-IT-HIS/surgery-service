package kr.co.seoulit.hisback.surgery.consent.repository;

import kr.co.seoulit.hisback.surgery.consent.entity.AnesthesiaConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 마취/수술 동의서 JPA 리포지토리
 * 컴포넌트 분리(SL2-42 consent)로 anesthesia 패키지에서 이동됨.
 */
public interface AnesthesiaConsentRepository extends JpaRepository<AnesthesiaConsent, Long> {

    /** 특정 수술의 동의서 목록 (SL2-54) */
    List<AnesthesiaConsent> findBySurgeryIdOrderByCreatedAtAsc(Long surgeryId);
}
