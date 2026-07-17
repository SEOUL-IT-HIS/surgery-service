package kr.co.seoulit.hisback.surgery.anesthesia.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import kr.co.seoulit.hisback.surgery.anesthesia.repository.AnesthesiaRecordRepository;
import org.springframework.stereotype.Service;

/**
 * 마취기록 서비스 구현체
 */
@Service
public class AnesthesiaRecordServiceImpl implements AnesthesiaRecordService {

    private final AnesthesiaRecordRepository anesthesiaRecordRepository;

    public AnesthesiaRecordServiceImpl(AnesthesiaRecordRepository anesthesiaRecordRepository) {
        this.anesthesiaRecordRepository = anesthesiaRecordRepository;
    }

    @Override
    public List<AnesthesiaRecordDto> getAnesthesiaRecords(String surgeryId) {
        return anesthesiaRecordRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AnesthesiaRecordDto createAnesthesiaRecord(AnesthesiaRecordDto request) {
        String anesthesiaId =
                request.getAnesthesiaId() != null ? request.getAnesthesiaId() : UUID.randomUUID().toString();
        AnesthesiaRecord record =
                AnesthesiaRecord.builder()
                        .anesthesiaId(anesthesiaId)
                        .surgeryId(request.getSurgeryId())
                        .anesthesiaTypeCd(request.getAnesthesiaTypeCd())
                        .asaGradeCd(request.getAsaGradeCd())
                        .build();
        return toDto(anesthesiaRecordRepository.save(record));
    }

    @Override
    public AnesthesiaRecordDto appendVitalSigns(String anesthesiaId, String vitalSignsEntry) {
        AnesthesiaRecord record =
                anesthesiaRecordRepository
                        .findById(anesthesiaId)
                        .orElseThrow(() -> new NoSuchElementException("마취기록을 찾을 수 없습니다: " + anesthesiaId));
        String existing = record.getVitalSignsLog();
        String appended =
                (existing == null ? "" : existing + "\n") + "[" + LocalDateTime.now() + "] " + vitalSignsEntry;
        record.setVitalSignsLog(appended);
        return toDto(anesthesiaRecordRepository.save(record));
    }

    private AnesthesiaRecordDto toDto(AnesthesiaRecord r) {
        return new AnesthesiaRecordDto(
                r.getAnesthesiaId(),
                r.getSurgeryId(),
                r.getAnesthesiaTypeCd(),
                r.getAsaGradeCd(),
                r.getVitalSignsLog(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
