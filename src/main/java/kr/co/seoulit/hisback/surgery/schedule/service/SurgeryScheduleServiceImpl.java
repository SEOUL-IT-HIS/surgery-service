package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
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

    public SurgeryScheduleServiceImpl(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
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

    /**
     * 수술 완료 처리.
     *
     * <p><b>SL2-72(수술 완료 → 수납 청구 연계)는 아직 붙어 있지 않다.</b> 이전에는 이벤트로
     * 발행했으나 브로커 운영 부담이 프로젝트 규모에 맞지 않아 제거했다. §21.3 이 REST 도
     * 허용하므로 {@code BillingServiceClient} 로 다시 붙이는 것이 다음 작업이며, 그때도
     * DB 저장을 먼저 끝내고 호출해야 한다 — 순서를 반대로 하면 저장이 실패했는데
     * "수술이 완료됐다"는 통보만 나가 수납 쪽에 유령 청구가 생긴다.</p>
     */
    @Override
    public SurgeryDto completeSurgery(String surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        surgery.setStatusCd(STATUS_COMPLETED);
        if (surgery.getActualEndDt() == null) {
            // actual_end_dt는 DDL상 DATE(§14.2 `_dt` = 날짜)라 LocalDate를 쓴다.
            surgery.setActualEndDt(LocalDate.now());
        }
        return toDto(surgeryRepository.save(surgery));
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
