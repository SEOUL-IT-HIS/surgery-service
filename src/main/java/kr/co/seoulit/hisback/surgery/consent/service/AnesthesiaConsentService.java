package kr.co.seoulit.hisback.surgery.consent.service;

import kr.co.seoulit.hisback.surgery.consent.dto.AnesthesiaConsentDto;
import kr.co.seoulit.hisback.surgery.consent.repository.AnesthesiaConsentRepository;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 수술/마취 동의서 서비스 로직 (SL2-42, API-SUR-004 중 동의서 부분)
 * anesthesia.service.AnesthesiaRecordService에서 분리됨 (컴포넌트 분리: SL2-42 consent).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnesthesiaConsentService {

    private final AnesthesiaConsentRepository anesthesiaConsentRepository;
    private final SurgeryRepository surgeryRepository;

    /** 수술/마취 동의서 확인 기록 작성 (SL2-38 / SL2-53 / API-SUR-004) */
    public AnesthesiaConsentDto createConsent(Long surgeryId, AnesthesiaConsentDto dto) {
        verifySurgeryExists(surgeryId);
        return AnesthesiaConsentDto.from(anesthesiaConsentRepository.save(dto.toEntity(surgeryId)));
    }

    /** 동의서 조회 (SL2-54) */
    @Transactional(readOnly = true)
    public List<AnesthesiaConsentDto> getConsents(Long surgeryId) {
        return anesthesiaConsentRepository.findBySurgeryIdOrderByCreatedAtAsc(surgeryId)
                .stream().map(AnesthesiaConsentDto::from).toList();
    }

    private void verifySurgeryExists(Long surgeryId) {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId);
        }
    }
}
