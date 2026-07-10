package kr.co.seoulit.hisback.surgery.schedule.service;

import kr.co.seoulit.hisback.surgery.client.WardServiceClient;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 수술 스케줄링 서비스 로직 (FR-SUR-002)
 * <p>등록/수정/취소/조회 및 응급 수술 등록, 수술실 일정 충돌 검사를 담당한다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SurgeryScheduleService {

    private static final Logger log = LoggerFactory.getLogger(SurgeryScheduleService.class);

    /** 동일 수술실 일정 충돌로 간주하는 앞뒤 여유시간(시간) */
    private static final long CONFLICT_WINDOW_HOURS = 2;

    private final SurgeryRepository surgeryRepository;
    private final WardServiceClient wardServiceClient;

    /** 수술 스케줄 신규 등록 (SL2-36 / API-SUR-001) */
    public SurgeryDto register(SurgeryDto dto) {
        validateScheduleConflict(dto.getOperatingRoom(), dto.getScheduledDt(), null);
        Surgery saved = surgeryRepository.save(dto.toEntity());
        notifyWard(saved);
        log.info("수술 스케줄 등록: surgeryId={}, room={}, scheduledDt={}",
                saved.getSurgeryId(), saved.getOperatingRoom(), saved.getScheduledDt());
        return SurgeryDto.from(saved);
    }

    /** 응급 수술 등록 (SL2-44) — 일정 충돌 시에도 우선 배정한다. */
    public SurgeryDto registerEmergency(SurgeryDto dto) {
        Surgery entity = dto.toEntity();
        entity.setEmergency(true);
        Surgery saved = surgeryRepository.save(entity);
        notifyWard(saved);
        log.info("응급 수술 등록: surgeryId={}, room={}", saved.getSurgeryId(), saved.getOperatingRoom());
        return SurgeryDto.from(saved);
    }

    /** 수술 일정 목록 조회 (SL2-25) — date가 없으면 전체 조회 */
    @Transactional(readOnly = true)
    public List<SurgeryDto> getSchedules(LocalDate date) {
        List<Surgery> surgeries = (date == null)
                ? surgeryRepository.findAll()
                : surgeryRepository.findByScheduledDtBetweenOrderByScheduledDtAsc(
                        date.atStartOfDay(), date.atTime(LocalTime.MAX));
        return surgeries.stream().map(SurgeryDto::from).toList();
    }

    /** 수술 일정 단건 조회 */
    @Transactional(readOnly = true)
    public SurgeryDto getSchedule(Long surgeryId) {
        return SurgeryDto.from(findOrThrow(surgeryId));
    }

    /** 수술 일정 수정 (SL2-37) */
    public SurgeryDto update(Long surgeryId, SurgeryDto dto) {
        Surgery surgery = findOrThrow(surgeryId);
        if (surgery.getStatus() != null && surgery.getStatus().isTerminal()) {
            throw new BusinessException("완료/취소된 수술은 수정할 수 없습니다.");
        }
        if (!surgery.isEmergency()) {
            validateScheduleConflict(dto.getOperatingRoom(), dto.getScheduledDt(), surgeryId);
        }
        surgery.setOperatingRoom(dto.getOperatingRoom());
        surgery.setSurgeonId(dto.getSurgeonId());
        surgery.setAnesthesiologistId(dto.getAnesthesiologistId());
        surgery.setSurgeryName(dto.getSurgeryName());
        surgery.setScheduledDt(dto.getScheduledDt());
        return SurgeryDto.from(surgery);
    }

    /** 수술 일정 취소 (SL2-33) — 상태를 취소됨으로 전이한다. */
    public void cancel(Long surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        if (surgery.getStatus() == SurgeryStatus.COMPLETED) {
            throw new BusinessException("이미 완료된 수술은 취소할 수 없습니다.");
        }
        surgery.setStatus(SurgeryStatus.CANCELLED);
        log.info("수술 취소: surgeryId={}", surgeryId);
    }

    private Surgery findOrThrow(Long surgeryId) {
        return surgeryRepository.findById(surgeryId)
                .orElseThrow(() -> new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId));
    }

    /**
     * 동일 수술실의 지정 시각 앞뒤 {@value #CONFLICT_WINDOW_HOURS}시간 이내에
     * 취소되지 않은 수술이 존재하면 충돌로 판단한다.
     *
     * @param excludeSurgeryId 수정 시 자기 자신은 검사에서 제외 (신규 등록이면 null)
     */
    private void validateScheduleConflict(String operatingRoom, LocalDateTime scheduledDt, Long excludeSurgeryId) {
        if (operatingRoom == null || scheduledDt == null) {
            return;
        }
        boolean conflict = surgeryRepository
                .findByOperatingRoomAndScheduledDtBetween(
                        operatingRoom,
                        scheduledDt.minusHours(CONFLICT_WINDOW_HOURS),
                        scheduledDt.plusHours(CONFLICT_WINDOW_HOURS))
                .stream()
                .filter(s -> s.getStatus() != SurgeryStatus.CANCELLED)
                .anyMatch(s -> !s.getSurgeryId().equals(excludeSurgeryId));
        if (conflict) {
            throw new BusinessException(
                    "해당 수술실(" + operatingRoom + ")의 요청 시간대에 이미 배정된 수술이 있습니다. 다른 시간/수술실을 선택하세요.");
        }
    }

    /** 병동관리 서비스에 수술 예약을 통보한다(장애 격리: 실패해도 등록 흐름은 유지). */
    private void notifyWard(Surgery surgery) {
        try {
            wardServiceClient.notifySurgeryScheduled(
                    surgery.getSurgeryId(), surgery.getPatientMpiId(), surgery.getScheduledDt());
        } catch (Exception e) {
            log.warn("병동관리 통보 실패(무시하고 진행): surgeryId={}, cause={}",
                    surgery.getSurgeryId(), e.getMessage());
        }
    }
}
