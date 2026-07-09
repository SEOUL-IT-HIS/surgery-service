package kr.co.seoulit.hisback.surgery.nursing.service;

import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.nursing.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursing.entity.NursingRecord;
import kr.co.seoulit.hisback.surgery.nursing.repository.NursingRecordRepository;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 수술간호기록 서비스 로직 (FR-SUR-008)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NursingRecordService {

    private final NursingRecordRepository nursingRecordRepository;
    private final SurgeryRepository surgeryRepository;

    /** 수술간호기록 작성 (SL2-58) */
    public NursingRecordDto create(Long surgeryId, NursingRecordDto dto) {
        verifySurgeryExists(surgeryId);
        return NursingRecordDto.from(nursingRecordRepository.save(dto.toEntity(surgeryId)));
    }

    /** 수술간호기록 수정 */
    public NursingRecordDto update(Long nursingRecordId, NursingRecordDto dto) {
        NursingRecord entity = findOrThrow(nursingRecordId);
        entity.setPatientPosition(dto.getPatientPosition());
        entity.setDisinfection(dto.getDisinfection());
        entity.setInstrumentsUsed(dto.getInstrumentsUsed());
        entity.setSpecimenInfo(dto.getSpecimenInfo());
        entity.setCirculatingNurseId(dto.getCirculatingNurseId());
        entity.setScrubNurseId(dto.getScrubNurseId());
        return NursingRecordDto.from(entity);
    }

    /**
     * 물품 카운트 대조 (SL2-59 / BR-013)
     * <p>시작 전/종료 후 수량을 비교하여 일치 여부를 기록한다. 불일치 시 remark(재확인 결과)로 해소한다.</p>
     *
     * @param resolved 불일치였으나 X-ray 확인 등으로 해소된 경우 true (일치로 간주)
     */
    public NursingRecordDto recordCount(Long nursingRecordId, Integer countInitial, Integer countFinal,
                                        String remark, boolean resolved) {
        NursingRecord entity = findOrThrow(nursingRecordId);
        entity.setCountInitial(countInitial);
        entity.setCountFinal(countFinal);
        boolean matched = countInitial != null && Objects.equals(countInitial, countFinal);
        entity.setCountMatched(matched || resolved);
        entity.setCountRemark(remark);
        return NursingRecordDto.from(entity);
    }

    /** 수술간호기록 조회 (SL2-61) */
    @Transactional(readOnly = true)
    public List<NursingRecordDto> getRecords(Long surgeryId) {
        return nursingRecordRepository.findBySurgeryIdOrderByCreatedAtAsc(surgeryId)
                .stream().map(NursingRecordDto::from).toList();
    }

    private NursingRecord findOrThrow(Long nursingRecordId) {
        return nursingRecordRepository.findById(nursingRecordId)
                .orElseThrow(() -> new BusinessException("수술간호기록을 찾을 수 없습니다. nursingRecordId=" + nursingRecordId));
    }

    private void verifySurgeryExists(Long surgeryId) {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId);
        }
    }
}
