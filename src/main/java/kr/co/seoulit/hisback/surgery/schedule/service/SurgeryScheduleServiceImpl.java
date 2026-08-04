package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.global.event.SurgeryCompletedEvent;
import kr.co.seoulit.hisback.surgery.global.event.SurgeryEventPublisher;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import org.springframework.stereotype.Service;

/**
 * 수술 스케줄링 서비스 구현체
 */
@Service
public class SurgeryScheduleServiceImpl implements SurgeryScheduleService {

    /** SURGERY_STATUS_CD: 01예약/02진행중/03완료/04취소 */
    private static final String STATUS_SCHEDULED = "01";
    private static final String STATUS_COMPLETED = "03";
    private static final String STATUS_CANCELLED = "04";

    private final SurgeryRepository surgeryRepository;
    private final SurgeryEventPublisher surgeryEventPublisher;

    public SurgeryScheduleServiceImpl(
            SurgeryRepository surgeryRepository, SurgeryEventPublisher surgeryEventPublisher) {
        this.surgeryRepository = surgeryRepository;
        this.surgeryEventPublisher = surgeryEventPublisher;
    }

    @Override
    public List<SurgeryDto> getSchedules(LocalDate surgeryDt) {
        List<Surgery> list =
                surgeryDt != null ? surgeryRepository.findBySurgeryDt(surgeryDt) : surgeryRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public SurgeryDto getSchedule(String surgeryId) {
        return toDto(findOrThrow(surgeryId));
    }

    @Override
    public SurgeryDto registerSchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        if (surgery.getStatusCd() == null) {
            surgery.setStatusCd(STATUS_SCHEDULED);
        }
        if (surgery.getEmergencyYn() == null) {
            surgery.setEmergencyYn("N");
        }
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto registerEmergencySchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        surgery.setStatusCd(STATUS_SCHEDULED);
        surgery.setEmergencyYn("Y");
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto updateSchedule(String surgeryId, SurgeryDto request) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setSurgeryDt(request.getSurgeryDt());
        surgery.setRoomCode(request.getRoomCode());
        surgery.setSurgeonId(request.getSurgeonId());
        surgery.setAnesthesiologistId(request.getAnesthesiologistId());
        surgery.setNurseId(request.getNurseId());
        surgery.setSurgeryName(request.getSurgeryName());
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto cancelSchedule(String surgeryId, String cancelReasonCd) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setStatusCd(STATUS_CANCELLED);
        surgery.setCancelReasonCd(cancelReasonCd);
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto assignSurgeon(String surgeryId, String surgeonId) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setSurgeonId(surgeonId);
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto assignRoom(String surgeryId, String roomCode) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setRoomCode(roomCode);
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto assignAnesthesiologist(String surgeryId, String anesthesiologistId) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setAnesthesiologistId(anesthesiologistId);
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public SurgeryDto assignNurse(String surgeryId, String nurseId) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setNurseId(nurseId);
        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    public List<SurgeryDto> getTodaySchedules() {
        return getSchedules(LocalDate.now());
    }

    @Override
    public SurgeryDto updateProgress(String surgeryId, String progressCd) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setProgressCd(progressCd);
        return toDto(surgeryRepository.save(surgery));
    }

    // SL2-72: 수술 완료 → 수납(Billing) 청구 연계.
    // DB 저장을 먼저 끝내고 그 다음에 이벤트를 발행한다. 순서를 반대로 하면 저장이 실패했는데
    // "수술이 완료됐다"는 이벤트만 나가버려 수납 쪽에 유령 청구가 생길 수 있다.
    @Override
    public SurgeryDto completeSurgery(String surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setStatusCd(STATUS_COMPLETED);
        if (surgery.getActualEndDt() == null) {
            // actual_end_dt는 DDL상 DATE(§14.2 `_dt` = 날짜)라 LocalDate를 쓴다.
            surgery.setActualEndDt(LocalDate.now());
        }
        Surgery saved = surgeryRepository.save(surgery);

        surgeryEventPublisher.publishSurgeryCompleted(
                new SurgeryCompletedEvent(
                        saved.getSurgeryId(),
                        saved.getPatientId(),
                        saved.getSurgTypeCd(),
                        saved.getSurgeryName(),
                        saved.getActualEndDt(),
                        LocalDateTime.now()));

        return toDto(saved);
    }

    private Surgery findOrThrow(String surgeryId) {
        return surgeryRepository
                .findById(surgeryId)
                .orElseThrow(() -> new NoSuchElementException("수술 일정을 찾을 수 없습니다: " + surgeryId));
    }

    private Surgery fromRequest(SurgeryDto request) {
        String surgeryId =
                request.getSurgeryId() != null ? request.getSurgeryId() : UUID.randomUUID().toString();
        return Surgery.builder()
                .surgeryId(surgeryId)
                .patientId(request.getPatientId())
                .surgeonId(request.getSurgeonId())
                .anesthesiologistId(request.getAnesthesiologistId())
                .nurseId(request.getNurseId())
                .roomCode(request.getRoomCode())
                .surgeryDt(request.getSurgeryDt())
                .surgeryName(request.getSurgeryName())
                .statusCd(request.getStatusCd())
                .emergencyYn(request.getEmergencyYn())
                .build();
    }

    private SurgeryDto toDto(Surgery s) {
        return new SurgeryDto(
                s.getSurgeryId(),
                s.getPatientId(),
                s.getSurgeonId(),
                s.getAnesthesiologistId(),
                s.getNurseId(),
                s.getRoomCode(),
                s.getSurgeryDt(),
                s.getStatusCd(),
                s.getProgressCd(),
                s.getCancelReasonCd(),
                s.getSurgTypeCd(),
                s.getSurgeryName(),
                s.getEmergencyYn(),
                s.getActualStartDt(),
                s.getActualEndDt(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
