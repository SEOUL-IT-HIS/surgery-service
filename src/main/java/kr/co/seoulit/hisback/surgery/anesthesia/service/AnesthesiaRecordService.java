package kr.co.seoulit.hisback.surgery.anesthesia.service;

import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.repository.AnesthesiaRecordRepository;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 마취기록 서비스 로직 (FR-SUR-003)
 * ※ 동의서(FR-SUR-006) 로직은 consent.service.AnesthesiaConsentService로 분리됨
 *   (컴포넌트 분리: SL2-42 consent)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnesthesiaRecordService {

    private final AnesthesiaRecordRepository anesthesiaRecordRepository;
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

    private void verifySurgeryExists(Long surgeryId) {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId);
        }
    }
}
