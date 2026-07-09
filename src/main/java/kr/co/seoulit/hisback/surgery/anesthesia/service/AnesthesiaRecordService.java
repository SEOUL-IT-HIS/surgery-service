package kr.co.seoulit.hisback.surgery.anesthesia.service;

import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaConsentDto;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.repository.AnesthesiaConsentRepository;
import kr.co.seoulit.hisback.surgery.anesthesia.repository.AnesthesiaRecordRepository;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 마취기록/동의서 서비스 로직 (FR-SUR-003 / FR-SUR-006)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnesthesiaRecordService {

    private final AnesthesiaRecordRepository anesthesiaRecordRepository;
    private final AnesthesiaConsentRepository anesthesiaConsentRepository;
    private final SurgeryRepository surgeryRepository;

    /** 마취기록 작성 (SL2-18 / SL2-21 / SL2-45) */
    public AnesthesiaRecordDto createRecord(Long surgeryId, AnesthesiaRecordDto dto) {
        verifySurgeryExists(surgeryId);
        return AnesthesiaRecordDto.from(anesthesiaRecordRepository.save(dto.toEntity(surgeryId)));
    }

    /** 마취기록 조회 (SL2-34) */
    @Transactional(readOnly = true)
    public List<AnesthesiaRecordDto> getRecords(Long surgeryId) {
        return anesthesiaRecordRepository.findBySurgeryIdOrderByCreatedAtAsc(surgeryId)
                .stream().map(AnesthesiaRecordDto::from).toList();
    }

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
