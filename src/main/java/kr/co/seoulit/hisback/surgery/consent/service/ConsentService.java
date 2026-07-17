package kr.co.seoulit.hisback.surgery.consent.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.consent.dto.ConsentDto;

/**
 * 동의서 관리 서비스 인터페이스 (구현체는 ConsentServiceImpl)
 */
public interface ConsentService {
    /** SL2-54: 동의서 목록조회 */
    List<ConsentDto> getConsents(String surgeryId);

    /** SL2-53: 동의서 확인(등록) */
    ConsentDto createConsent(ConsentDto request);
}
