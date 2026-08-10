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
 * 수술기록 서비스 구현체 (SL2-51)
 *
 * <p>마취기록·간호기록과 달리 <b>수정을 허용</b>한다. 집도의가 수술 직후 초안(01)으로 남기고
 * 나중에 다듬어 확정하는 것이 실제 업무 흐름이기 때문이다. 대신 확정 이후의 변경 제한은
 * opStatusCd 로 표현할 여지를 남겨뒀다.</p>
 *
 * <p>수술명(procedureName)을 코드와 함께 저장하는 이유 — 시술 마스터의 명칭이 나중에 바뀌어도
 * 그 수술 당시 기록된 이름이 남아야 한다. 다른 서비스 데이터를 복제해 두는 것과는 성격이 다르다.</p>
 */
@Service
public class OperativeRecordServiceImpl implements OperativeRecordService {

    /** OP_STATUS_CD 01=초안. 작성 직후 기본 상태이며, 확정은 별도 행위다. */
    private static final String STATUS_DRAFT = "01";

    private final OperativeRecordRepository operativeRecordRepository;

    public OperativeRecordServiceImpl(OperativeRecordRepository operativeRecordRepository) {
        this.operativeRecordRepository = operativeRecordRepository;
    }

    /** SL2-57: 특정 수술의 수술기록 목록 조회. */
    @Override
    public List<OperativeRecordDto> getOperativeRecords(String surgeryId) {
        return operativeRecordRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * SL2-57: 수술기록 단건 조회
     *
     * <p>목록과 달리 없으면 예외를 던진다 — 특정 건을 지목한 요청이라 빈 결과가 정상 응답일 수 없다.
     * 아래 updateOperativeRecord 와 같은 방식으로 대상을 찾는다.</p>
     */
    @Override
    public OperativeRecordDto getOperativeRecord(String recordId) {
        return toDto(
                operativeRecordRepository
                        .findById(recordId)
                        .orElseThrow(() -> new NoSuchElementException("수술기록을 찾을 수 없습니다: " + recordId)));
    }

    /** SL2-55: 수술기록 작성 — 상태를 안 보내면 초안(01)으로 시작한다. */
    @Override
    public OperativeRecordDto createOperativeRecord(OperativeRecordDto request) {
        // PK는 내부 식별자라 서버가 UUID로 채번한다(§14.2 `_id` → VARCHAR2(36))
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

    /**
     * SL2-56: 수술기록 수정
     *
     * <p>시술 코드·명칭은 무조건 덮어쓰지만 상태(opStatusCd)는 값이 올 때만 바꾼다.
     * 내용만 고치려고 보낸 요청이 상태까지 초기화하는 사고를 막기 위해서다.</p>
     */
    @Override
    public OperativeRecordDto updateOperativeRecord(String recordId, OperativeRecordDto request) {
        OperativeRecord record =
                operativeRecordRepository
                        .findById(recordId)
                        .orElseThrow(() -> new NoSuchElementException("수술기록을 찾을 수 없습니다: " + recordId));
        record.setProcedureCd(request.getProcedureCd());
        record.setProcedureName(request.getProcedureName());
        // 상태는 선택 항목 — null 이면 기존 값을 유지한다
        if (request.getOpStatusCd() != null) {
            record.setOpStatusCd(request.getOpStatusCd());
        }
        return toDto(operativeRecordRepository.save(record));
    }

    /** 엔티티 → DTO 변환. 필드명은 프론트 types.ts 의 OperativeRecord 와 1:1로 맞춘다. */
    private OperativeRecordDto toDto(OperativeRecord r) {
        return new OperativeRecordDto(
                r.getRecordId(), r.getSurgeryId(), r.getProcedureCd(), r.getProcedureName(), r.getOpStatusCd(), r.getCreatedAt(), r.getUpdatedAt());
    }
}
