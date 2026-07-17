package kr.co.seoulit.hisback.surgery.nursingrecord.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.nursingrecord.dto.NursingRecordDto;
import kr.co.seoulit.hisback.surgery.nursingrecord.entity.NursingRecord;
import kr.co.seoulit.hisback.surgery.nursingrecord.repository.NursingRecordRepository;
import org.springframework.stereotype.Service;

/**
 * 수술간호기록 서비스 구현체
 */
@Service
public class NursingRecordServiceImpl implements NursingRecordService {

    private final NursingRecordRepository nursingRecordRepository;

    public NursingRecordServiceImpl(NursingRecordRepository nursingRecordRepository) {
        this.nursingRecordRepository = nursingRecordRepository;
    }

    @Override
    public List<NursingRecordDto> getNursingRecords(String surgeryId) {
        return nursingRecordRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NursingRecordDto createNursingRecord(NursingRecordDto request) {
        String nursingRecordId =
                request.getNursingRecordId() != null
                        ? request.getNursingRecordId()
                        : UUID.randomUUID().toString();
        NursingRecord record =
                NursingRecord.builder()
                        .nursingRecordId(nursingRecordId)
                        .surgeryId(request.getSurgeryId())
                        .itemCountResultCd(request.getItemCountResultCd())
                        .specimenBarcode(request.getSpecimenBarcode())
                        .specimenTypeCd(request.getSpecimenTypeCd())
                        .recordStatusCd(
                                request.getRecordStatusCd() != null ? request.getRecordStatusCd() : "01")
                        .build();
        return toDto(nursingRecordRepository.save(record));
    }

    private NursingRecordDto toDto(NursingRecord r) {
        return new NursingRecordDto(
                r.getNursingRecordId(),
                r.getSurgeryId(),
                r.getItemCountResultCd(),
                r.getSpecimenBarcode(),
                r.getSpecimenTypeCd(),
                r.getRecordStatusCd(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
