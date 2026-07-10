package kr.co.seoulit.hisback.surgery.operation.service;

import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.operation.dto.OperativeRecordDto;
import kr.co.seoulit.hisback.surgery.operation.entity.OperativeRecord;
import kr.co.seoulit.hisback.surgery.operation.repository.OperativeRecordRepository;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수술기록지 서비스 로직 (FR-SUR-007)
 * <p>확정(finalized) 후에는 수정 시 정정 이력이 남도록 updated_at을 갱신한다. BR-014: 24시간 이내 확정 권고.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OperativeRecordService {

    private final OperativeRecordRepository operativeRecordRepository;
    private final SurgeryRepository surgeryRepository;

    /** 수술기록지 작성 (SL2-55 / API-SUR-005) */
    public OperativeRecordDto create(Long surgeryId, OperativeRecordDto dto) {
        verifySurgeryExists(surgeryId);
        OperativeRecord entity = dto.toEntity(surgeryId);
        if (entity.isFinalized()) {
            entity.setFinalizedDt(LocalDateTime.now());
        }
        return OperativeRecordDto.from(operativeRecordRepository.save(entity));
    }

    /** 수술기록지 수정 (SL2-56) */
    public OperativeRecordDto update(Long recordId, OperativeRecordDto dto) {
        OperativeRecord entity = findOrThrow(recordId);
        entity.setProcedureName(dto.getProcedureName());
        entity.setProcedureDetail(dto.getProcedureDetail());
        entity.setFindings(dto.getFindings());
        entity.setPostoperativeDiagnosis(dto.getPostoperativeDiagnosis());
        entity.setBloodLossMl(dto.getBloodLossMl());
        entity.setSurgeonId(dto.getSurgeonId());
        // 미확정 → 확정 전이 시 확정일시 기록
        if (dto.isFinalized() && !entity.isFinalized()) {
            entity.setFinalized(true);
            entity.setFinalizedDt(LocalDateTime.now());
        }
        return OperativeRecordDto.from(entity);
    }

    /** 수술기록지 조회 (SL2-57) */
    @Transactional(readOnly = true)
    public List<OperativeRecordDto> getRecords(Long surgeryId) {
        return operativeRecordRepository.findBySurgeryIdOrderByCreatedAtAsc(surgeryId)
                .stream().map(OperativeRecordDto::from).toList();
    }

    private OperativeRecord findOrThrow(Long recordId) {
        return operativeRecordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("수술기록지를 찾을 수 없습니다. recordId=" + recordId));
    }

    private void verifySurgeryExists(Long surgeryId) {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId);
        }
    }
}
