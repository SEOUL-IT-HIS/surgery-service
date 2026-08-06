package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import kr.co.seoulit.hisback.surgery.schedule.type.SurgeryStatus;
import org.springframework.stereotype.Service;

/**
 * 수술 스케줄링 서비스 구현체
 *
 * <p>상태 상수는 {@link SurgeryStatus} 로 옮겼다 — 요청접수(00)가 추가되면서
 * 여러 클래스가 같은 코드값을 참조하게 되어 한 곳에서 관리한다.</p>
 */
@Service
public class SurgeryScheduleServiceImpl implements SurgeryScheduleService {

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

    /**
     * SL2-36: 수술 요청 등록 (진료가 호출)
     *
     * <p>수술실이 아직 배정되지 않았으므로 '요청접수'로 시작한다. 수술실 담당자가
     * {@link #assignSurgery} 로 배정해야 '예약'이 되며, 그 전까지는 배정 대기 목록에 뜬다.</p>
     *
     * <p>상태와 응급여부는 클라이언트 값을 쓰지 않고 <b>호출한 엔드포인트가 결정</b>한다.
     * statusCd 를 받아주면 배정을 건너뛴 등록이 생기고, emergencyYn 을 받아주면 일반 요청이
     * 응급으로 둔갑해 배정 우선순위를 가로챈다.</p>
     */
    @Override
    public SurgeryDto registerSchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        surgery.setStatusCd(SurgeryStatus.REQUESTED);
        surgery.setEmergencyYn("N");
        return toDto(surgeryRepository.save(surgery));
    }

    /** SL2-44: 응급 수술 요청 등록 (응급실이 호출). 동일하게 요청접수이며 emergencyYn=Y 로 고정한다. */
    @Override
    public SurgeryDto registerEmergencySchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        surgery.setStatusCd(SurgeryStatus.REQUESTED);
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

        // SL2-178: 취소 가능한 상태인지 먼저 검사한다.
        //
        // 허용 목록으로 쓰는 이유 — 나중에 상태가 추가돼도 기본이 '차단'이라 안전하다.
        // 차단 목록으로 쓰면 새 상태가 생길 때마다 여기에 추가하는 걸 잊기 쉽다.
        //
        //   00 요청접수 → 허용 (업무상 '반려')
        //   01 예약     → 허용 (배정은 됐지만 아직 시작 전)
        //   02 진행중   → 차단 (환자가 이미 수술대에 있다)
        //   03 완료     → 차단 (끝난 일)
        //   04 취소     → 차단 (이미 취소됨)
        //
        // 진행중 수술이 실제로 중단되는 경우(환자 상태 악화 등)는 '취소'가 아니라
        // 별도 상태로 다뤄야 한다 — 여기서 함께 처리하면 통계에서 요청 반려와
        // 수술 중단이 섞인다. 해당 상태 코드는 아직 정의되지 않았다.
        if (!SurgeryStatus.REQUESTED.equals(surgery.getStatusCd())
                && !SurgeryStatus.SCHEDULED.equals(surgery.getStatusCd())) {
            throw new IllegalArgumentException(
                    "요청접수·예약 상태에서만 취소할 수 있습니다: " + surgery.getStatusCd());
        }

        surgery.setStatusCd(SurgeryStatus.CANCELLED);
        surgery.setCancelReasonCd(cancelReasonCd);
        // 배정 정보(수술실·집도의·마취의·간호사)는 여기서 지우지 않는다.
        // 일괄 해제 여부는 SL2-179 에서 별도로 다룬다 — 이력 보존과 자원 반납이
        // 상충해 판단이 필요하고, 두 필드를 함께 바꾸므로 @Transactional 도 같이 검토해야 한다.
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

    /** SL2-225: 배정 대기 목록 — 응급이 먼저 오도록 리포지토리에서 정렬해 내려준다. */
    @Override
    public List<SurgeryDto> getRequestedSchedules() {
        return surgeryRepository
                .findByStatusCdOrderByEmergencyYnDescSurgeryDtAsc(SurgeryStatus.REQUESTED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * SL2-15: 수술 배정 (요청접수 → 예약)
     *
     * <p>수술실은 배정의 핵심이라 필수, 마취의·간호사는 나중에 채워도 되므로 선택이다.
     * 요청자가 올린 희망일은 수술실 사정에 맞춰 조정할 수 있고, 값이 없으면 기존 일자를 유지한다.</p>
     *
     * <p>환자·집도의를 건드리지 않는 이유 — 진료·응급실이 확정한 값이라 배정에서 바꾸면
     * 요청 자체가 뒤바뀐다. 집도의 변경이 필요하면 별도 API(/surgeon)나 수정(PUT)으로 처리한다.</p>
     */
    @Override
    public SurgeryDto assignSurgery(String surgeryId, SurgeryDto request) {
        Surgery surgery = findOrThrow(surgeryId);
        if (!SurgeryStatus.REQUESTED.equals(surgery.getStatusCd())) {
            throw new IllegalArgumentException("요청접수 상태에서만 배정할 수 있습니다: " + surgery.getStatusCd());
        }
        if (request.getRoomCode() == null || request.getRoomCode().isBlank()) {
            throw new IllegalArgumentException("수술실은 필수입니다");
        }

        surgery.setRoomCode(request.getRoomCode());
        if (request.getAnesthesiologistId() != null) {
            surgery.setAnesthesiologistId(request.getAnesthesiologistId());
        }
        if (request.getNurseId() != null) {
            surgery.setNurseId(request.getNurseId());
        }
        if (request.getSurgeryDt() != null) {
            surgery.setSurgeryDt(request.getSurgeryDt());
        }
        surgery.setStatusCd(SurgeryStatus.SCHEDULED);
        return toDto(surgeryRepository.save(surgery));
    }

    /** 수술 시작 — 예약 상태에서만 가능하며 실제 시작일을 남긴다. */
    @Override
    public SurgeryDto startSurgery(String surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        if (!SurgeryStatus.SCHEDULED.equals(surgery.getStatusCd())) {
            throw new IllegalArgumentException("예약 상태에서만 시작할 수 있습니다: " + surgery.getStatusCd());
        }
        surgery.setStatusCd(SurgeryStatus.IN_PROGRESS);
        if (surgery.getActualStartDt() == null) {
            // actual_start_dt는 DDL상 DATE(§14.2 `_dt` = 날짜)라 LocalDate를 쓴다.
            surgery.setActualStartDt(LocalDate.now());
        }
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
        surgery.setStatusCd(SurgeryStatus.COMPLETED);
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
