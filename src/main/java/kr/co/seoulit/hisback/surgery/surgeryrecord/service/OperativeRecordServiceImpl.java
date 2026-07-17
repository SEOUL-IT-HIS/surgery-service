package kr.co.seoulit.hisback.surgery.surgeryrecord.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.surgeryrecord.dto.OperativeRecordDto;
import kr.co.seoulit.hisback.surgery.surgeryrecord.entity.OperativeRecord;
import kr.co.seoulit.hisback.surgery.surgeryrecord.repository.OperativeRecordRepository;
import org.springframework.stereotype.Service;

/**
 * 수술기록 서비스 구현체
 */
@Service
public class OperativeRecordServiceImpl implements OperativeRecordService {

    private static final String STATUS_DRAFT = "01";

    private final OperativeRecordRepository operativeRecordRepository;

    public OperativeRecordServiceImpl(OperativeRecordRepository operativeRecordRepository) {
        this.operativeRecordRepository = operativeRecordRepository;
    }

    @Override
    public List<OperativeRecordDto> getOperativeRecords(String surgeryId) {
        return operativeRecordRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OperativeRecordDto createOperativeRecord(OperativeRecordDto request) {
        String recordId = request.getRecordId() != null ? request.getRecordId() : UUID.randomUUID().toString();
        OperativeRecord record =
                OperativeRecord.builder()
                        .recordId(recordId)
                        .surgeryId(request.getSurgeryId())
                        .procedureCd(request.getProcedureCd())
                        .procedureName(request.getProcedureName())
                        .opStatusCd(request.getOpStatusCd() != null ? request.getOpStatusCd() : STATUS_DRAFT)
                        .build();
        return toDto(operativeRecordRepository.save(record));
    }

    @Override
    public OperativeRecordDto updateOperativeRecord(String recordId, OperativeRecordDto request) {
        OperativeRecord record =
                operativeRecordRepository
                        .findById(recordId)
                        .orElseThrow(() -> new NoSuchElementException("수술기록을 찾을 수 없습니다: " + recordId));
        record.setProcedureCd(request.getProcedureCd());
        record.setProcedureName(request.getProcedureName());
        if (request.getOpStatusCd() != null) {
            record.setOpStatusCd(request.getOpStatusCd());
        }
        return toDto(operativeRecordRepository.save(record));
    }

    private OperativeRecordDto toDto(OperativeRecord r) {
        return new OperativeRecordDto(
                r.getRecordId(), r.getSurgeryId(), r.getProcedureCd(), r.getProcedureName(), r.getOpStatusCd(), r.getCreatedAt(), r.getUpdatedAt());
    }
}
