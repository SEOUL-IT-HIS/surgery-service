package kr.co.seoulit.hisback.surgery.consent.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.consent.dto.ConsentDto;

/**
 * 동의서 관리 서비스 인터페이스 (구현체는 ConsentServiceImpl)
 */
public interface ConsentService {
    /** SL2-54: 동의서 목록조회 */
    List<ConsentDto> getConsents(String surgeryId);

    /** SL2-222: 환자별 동의서 이력 조회 */
    List<ConsentDto> getConsentsByPatient(String patientId);

    /** 동의서 단건 조회 */
    ConsentDto getConsent(String consentId);

    /** SL2-53: 동의서 확인(등록) */
    ConsentDto createConsent(ConsentDto request);
}
