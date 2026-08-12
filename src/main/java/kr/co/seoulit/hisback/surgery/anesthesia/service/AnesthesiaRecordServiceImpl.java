package kr.co.seoulit.hisback.surgery.anesthesia.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.anesthesia.dto.AnesthesiaRecordDto;
import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import kr.co.seoulit.hisback.surgery.anesthesia.repository.AnesthesiaRecordRepository;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryGuard;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

/**
 * 마취기록 서비스 구현체 (SL2-3)
 *
 * <p>수정(update) API 가 없는 이유 — 마취기록은 시술 중 관찰한 사실의 기록이라 덮어쓰지 않는다.
 * 활력징후는 {@link #appendVitalSigns} 로 <b>덧붙이기만</b> 하고, 이미 적힌 내용은 지우지 않는다.
 * 진료기록의 성격상 사후 변조 여지를 남기지 않기 위해서다(§21.6).</p>
 *
 * <p>한 수술에 마취기록이 여러 건일 수 있어 조회가 목록을 돌려준다. 마취 방식을 도중에
 * 바꾸면 기록을 새로 남기기 때문이다.</p>
 */
@Service
public class AnesthesiaRecordServiceImpl implements AnesthesiaRecordService {

    private final AnesthesiaRecordRepository anesthesiaRecordRepository;

    /** SL2-223: 하위 목록 조회 전에 수술 존재를 확인한다 */
    private final SurgeryGuard surgeryGuard;

    public AnesthesiaRecordServiceImpl(
            AnesthesiaRecordRepository anesthesiaRecordRepository, SurgeryGuard surgeryGuard) {
        this.anesthesiaRecordRepository = anesthesiaRecordRepository;
        this.surgeryGuard = surgeryGuard;
    }

    /**
     * SL2-34/246: 특정 수술의 마취기록 목록 (페이지 단위).
     *
     * <p>SL2-223: 수술이 없으면 빈 페이지가 아니라 404 다. 잘못된 식별자를 "자료 없음"으로
     * 답하면 오타가 조용히 넘어간다(2026-08-12 결정).</p>
     */
    @Override
    public PageResponse<AnesthesiaRecordDto> getAnesthesiaRecords(
            String surgeryId, Pageable pageable) {
        surgeryGuard.requireExists(surgeryId);
        Page<AnesthesiaRecord> result =
                anesthesiaRecordRepository.findBySurgeryId(surgeryId, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    /**
     * SL2-247: 마취기록 단건 조회
     *
     * <p>목록과 달리 없으면 예외를 던진다 — 특정 건을 지목한 요청이라 빈 결과가 정상 응답일 수 없다.
     * 목록 조회에서 빈 배열이 정상인 것과는 상황이 다르다.</p>
     */
    @Override
    public AnesthesiaRecordDto getAnesthesiaRecord(String anesthesiaId) {
        return toDto(
                anesthesiaRecordRepository
                        .findById(anesthesiaId)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.ANESTHESIA_RECORD_NOT_FOUND, anesthesiaId)));
    }

    /**
     * SL2-18: 마취기록 생성
     *
     * <p>활력징후(vitalSignsLog)를 여기서 받지 않는 이유 — 생성 시점에는 아직 관찰된 값이 없다.
     * 기록의 껍데기를 먼저 만들고, 이후 appendVitalSigns 로 시간순으로 쌓는다.</p>
     */
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

    /**
     * SL2-18: 활력징후 기록 추가
     *
     * <p>기존 로그 뒤에 <b>줄바꿈으로 이어 붙인다</b> — 덮어쓰지 않는다. 시각을 서버가 찍는
     * 이유는 클라이언트 시계를 믿을 수 없기 때문이다. 여러 단말에서 기록해도 한 줄기로 정렬된다.</p>
     */
    @Override
    public AnesthesiaRecordDto appendVitalSigns(String anesthesiaId, String vitalSignsEntry) {
        AnesthesiaRecord record =
                anesthesiaRecordRepository
                        .findById(anesthesiaId)
                        .orElseThrow(
                                () -> new BusinessException(
                                        ErrorCode.ANESTHESIA_RECORD_NOT_FOUND, anesthesiaId));
        String existing = record.getVitalSignsLog();
        // 첫 기록이면 앞에 줄바꿈을 넣지 않는다 — 빈 줄로 시작하는 로그를 만들지 않기 위해
        String appended =
                (existing == null ? "" : existing + "\n") + "[" + LocalDateTime.now() + "] " + vitalSignsEntry;
        record.setVitalSignsLog(appended);
        return toDto(anesthesiaRecordRepository.save(record));
    }

    /** 엔티티 → DTO 변환. 필드명은 프론트 types.ts 의 AnesthesiaRecord 와 1:1로 맞춘다. */
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
