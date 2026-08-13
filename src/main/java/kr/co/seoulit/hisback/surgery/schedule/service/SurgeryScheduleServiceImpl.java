package kr.co.seoulit.hisback.surgery.schedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.common.cache.CommonCodeCache;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryStatusHistoryDto;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatusHistory;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryStatusHistoryRepository;
import kr.co.seoulit.hisback.surgery.schedule.type.StatusChangeType;
import kr.co.seoulit.hisback.surgery.schedule.type.SurgeryStatus;
import kr.co.seoulit.hisback.surgery.schedule.type.SurgeryStatusTransition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술 스케줄링 서비스 구현체
 *
 * <p>상태 상수는 {@link SurgeryStatus} 로 옮겼다 — 요청접수(00)가 추가되면서
 * 여러 클래스가 같은 코드값을 참조하게 되어 한 곳에서 관리한다.</p>
 */
@Service
public class SurgeryScheduleServiceImpl implements SurgeryScheduleService {

    /**
     * 취소·반려 사유 코드 그룹 (SL2-227).
     *
     * <p>admin 에 아직 등록되지 않았다. 등록 전까지는 검증이 건너뛰어지고,
     * 화면 선택지도 비어 보인다. 등록은 수술 담당이 한다(2026-08-10 합의).</p>
     */
    private static final String GROUP_CANCEL_REASON = "SURGERY_CANCEL_CD";

    private final SurgeryRepository surgeryRepository;
    private final SurgeryStatusHistoryRepository historyRepository;
    private final CommonCodeCache commonCodeCache;

    public SurgeryScheduleServiceImpl(
            SurgeryRepository surgeryRepository,
            SurgeryStatusHistoryRepository historyRepository,
            CommonCodeCache commonCodeCache) {
        this.surgeryRepository = surgeryRepository;
        this.historyRepository = historyRepository;
        this.commonCodeCache = commonCodeCache;
    }

    /**
     * SL2-282: 상태변경 이력을 한 행 남긴다.
     *
     * <p>여덟 군데에서 같은 코드를 반복하지 않으려고 한곳으로 모았다. 호출부는
     * <b>값을 바꾸기 전에</b> 이전 값을 잡아 넘겨야 한다 — set 뒤에 읽으면 before 와 after 가
     * 같은 값이 되어 이력이 무의미해진다.</p>
     *
     * <p>값이 그대로면 남기지 않는다. 같은 버튼을 두 번 눌러도 줄이 늘지 않게 하기 위해서다.</p>
     *
     * <p>{@code changedBy} 는 아직 채우지 않는다. 수술 백엔드에 로그인 세션이 없어 서버가
     * 알아낼 방법이 없고, 프론트에서 받기로 했으나 그러려면 API 계약이 바뀐다.
     * 컬럼은 nullable 로 만들어 뒀으므로 나중에 채워도 기존 행은 그대로 둔다.</p>
     */
    /**
     * SL2-281: 상태 전이가 규칙에 맞는지 검사한다. 어긋나면 400 SUR039.
     *
     * <p>규칙 자체는 {@link SurgeryStatusTransition} 에 있다 — 여기서는 규칙을 묻고
     * 예외로 옮기는 일만 한다. 어긋난 전이를 알려주는 상세 문구는 detail 로만 남기고
     * 응답에는 싣지 않는다(§15.1).</p>
     */
    private void requireTransition(String from, String to) {
        if (!SurgeryStatusTransition.isAllowed(from, to)) {
            throw new BusinessException(
                    ErrorCode.INVALID_SURGERY_STATUS, "전이 불가 " + from + "→" + to);
        }
    }

    private void recordHistory(
            String surgeryId, String statusType, String beforeCd, String afterCd, String reasonCd) {
        if (afterCd != null && afterCd.equals(beforeCd)) {
            return;
        }
        historyRepository.save(
                SurgeryStatusHistory.builder()
                        .historyId(UUID.randomUUID().toString())
                        .surgeryId(surgeryId)
                        .statusType(statusType)
                        .beforeCd(beforeCd)
                        .afterCd(afterCd)
                        .reasonCd(reasonCd)
                        .build());
    }

    /**
     * SL2-282: 한 수술의 상태변경 이력을 조회한다.
     *
     * <p>수술이 없으면 빈 목록이 아니라 404 를 돌려준다 — 있지도 않은 수술의 이력을
     * "없음"으로 답하면 오타로 잘못된 식별자를 넣었을 때 알아채지 못한다.</p>
     *
     * <p>{@code statusType} 이 null 이면 STATUS·PROGRESS 를 섞어 시간 역순으로 모두 돌려준다.
     * 두 종류를 한 테이블에 모았기 때문에, 큰 상태 전이만 보고 싶은 화면은 필터를 건다.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<SurgeryStatusHistoryDto> getStatusHistory(String surgeryId, String statusType) {
        findOrThrow(surgeryId);

        List<SurgeryStatusHistory> list =
                (statusType != null && !statusType.isBlank())
                        ? historyRepository.findBySurgeryIdAndStatusTypeOrderByChangedAtDesc(
                                surgeryId, statusType)
                        : historyRepository.findBySurgeryIdOrderByChangedAtDesc(surgeryId);

        return list.stream().map(this::toHistoryDto).collect(Collectors.toList());
    }

    /** 이력 엔티티 → DTO. 필드명이 1:1이라 그대로 옮긴다. */
    private SurgeryStatusHistoryDto toHistoryDto(SurgeryStatusHistory h) {
        return new SurgeryStatusHistoryDto(
                h.getHistoryId(),
                h.getSurgeryId(),
                h.getStatusType(),
                h.getBeforeCd(),
                h.getAfterCd(),
                h.getReasonCd(),
                h.getChangedBy(),
                h.getChangedAt(),
                h.getCreatedAt(),
                h.getUpdatedAt());
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
    @Transactional
    public SurgeryDto registerSchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        surgery.setStatusCd(SurgeryStatus.REQUESTED);
        surgery.setEmergencyYn("N");
        Surgery saved = surgeryRepository.save(surgery);
        // 등록은 저장 뒤에 남긴다 — PK 가 저장 시점에 정해지기 때문이다.
        // 최초 등록이라 이전 값이 없어 beforeCd 는 null 이다.
        recordHistory(saved.getSurgeryId(), StatusChangeType.STATUS, null, SurgeryStatus.REQUESTED, null);
        return toDto(saved);
    }

    /** SL2-44: 응급 수술 요청 등록 (응급실이 호출). 동일하게 요청접수이며 emergencyYn=Y 로 고정한다. */
    @Override
    @Transactional
    public SurgeryDto registerEmergencySchedule(SurgeryDto request) {
        Surgery surgery = fromRequest(request);
        surgery.setStatusCd(SurgeryStatus.REQUESTED);
        surgery.setEmergencyYn("Y");
        Surgery saved = surgeryRepository.save(surgery);
        recordHistory(saved.getSurgeryId(), StatusChangeType.STATUS, null, SurgeryStatus.REQUESTED, null);
        return toDto(saved);
    }

    /**
     * SL2-37: 수술 스케줄 수정 (SL2-188 결과·연관 배정 정보 갱신)
     *
     * <p><b>전체 교체(PUT)다.</b> 프론트 {@code UpdateSurgeryRequest} 도 같은 계약이라,
     * 보내지 않은 배정 항목은 비워진다. 일부만 바꾸려면 배정 전용 PATCH(/room, /surgeon,
     * /anesthesiologist, /nurse)를 쓴다 — 그쪽은 받은 값만 반영한다.</p>
     *
     * <h3>수정할 수 없는 것</h3>
     * <ul>
     *   <li><b>환자</b> — 환자가 바뀌면 그것은 다른 수술이다. 같은 행을 고쳐 쓰면 이 수술에
     *       달린 동의서·마취기록이 엉뚱한 환자의 것이 된다. 값이 다르면 거부한다.</li>
     *   <li><b>상태</b> — 전이 API 로만 바뀐다. 여기서 바꾸면 이력이 남지 않는다(SL2-282).</li>
     *   <li><b>응급 여부</b> — 배정 우선순위를 뒤집는 값이라 수정으로 다루지 않는다.
     *       요청 본문에 실려 와도 무시한다.</li>
     * </ul>
     *
     * <p><b>끝난 수술은 고칠 수 없다.</b> 완료·취소는 의무기록으로 확정된 상태라
     * 조용히 덮어쓰면 안 된다. 정정이 필요하면 별도 업무로 다뤄야 하며 그 요구사항은 아직 없다.</p>
     */
    @Override
    @Transactional
    public SurgeryDto updateSchedule(String surgeryId, SurgeryDto request) {
        Surgery surgery = findOrThrow(surgeryId);

        // 요청접수(00)·예약(01)만 수정 대상이다. 진행중은 수술이 이미 시작돼 일정을
        //   바꾸는 것이 무의미하고, 완료·취소는 확정된 기록이다.
        if (!SurgeryStatus.REQUESTED.equals(surgery.getStatusCd())
                && !SurgeryStatus.SCHEDULED.equals(surgery.getStatusCd())) {
            throw new BusinessException(
                    ErrorCode.INVALID_SURGERY_STATUS, "수정 시도 상태=" + surgery.getStatusCd());
        }

        // 환자 변경은 거부한다. 무시하고 넘어가면 요청자는 바뀐 줄 알고 있게 된다.
        if (request.getPatientId() != null
                && !request.getPatientId().equals(surgery.getPatientId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "환자는 수정할 수 없습니다");
        }

        surgery.setSurgeryDt(request.getSurgeryDt());
        surgery.setSurgeonId(request.getSurgeonId());
        surgery.setSurgeryName(request.getSurgeryName());
        // SL2-188: 프론트가 보내는데 반영되지 않던 항목이다. 계약에 있으면 반영해야 한다.
        surgery.setSurgeryTypeCd(request.getSurgeryTypeCd());

        // 연관 배정 정보 — 전체 교체 계약이라 보내지 않은 값은 해제된다.
        //   수술실을 비우면 배정 대기로 되돌아가는 셈이지만 상태(01)는 그대로다.
        //   상태까지 되돌릴지는 취소 시 일괄 해제(SL2-179)와 함께 정해야 할 문제라
        //   여기서 임의로 정하지 않는다.
        surgery.setRoomCode(request.getRoomCode());
        surgery.setAnesthesiologistId(request.getAnesthesiologistId());
        surgery.setNurseId(request.getNurseId());

        return toDto(surgeryRepository.save(surgery));
    }

    @Override
    @Transactional
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
        //
        // SL2-281: 위 표는 SurgeryStatusTransition 으로 옮겼다. 규칙이 메서드마다
        // 흩어져 있으면 새 상태가 생겼을 때 고칠 곳을 놓친다.
        String before = surgery.getStatusCd();
        requireTransition(before, SurgeryStatus.CANCELLED);

        // SL2-227: 사유 코드가 왔다면 admin 에 등록된 값인지 확인한다.
        //
        //   그룹이 아직 admin 에 없으면 검증을 건너뛴다 — 코드 등록 전이라고 반려 업무를
        //   막을 수는 없다. 수술실·장비 상태코드에서 쓴 것과 같은 판단이다.
        //   그룹이 생기는 순간 이 검증이 저절로 살아난다.
        if (reasonCd != null
                && !reasonCd.isBlank()
                && commonCodeCache.hasGroup(GROUP_CANCEL_REASON)
                && !commonCodeCache.isValid(GROUP_CANCEL_REASON, reasonCd)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST, GROUP_CANCEL_REASON + "=" + reasonCd);
        }

        // set 하기 전에 이전 값을 잡아야 한다 — 뒤에 읽으면 before 와 after 가 같아진다
        surgery.setStatusCd(SurgeryStatus.CANCELLED);
        surgery.setCancelReasonCd(cancelReasonCd);
        // 배정 정보(수술실·집도의·마취의·간호사)는 여기서 지우지 않는다.
        // 일괄 해제 여부는 SL2-179 에서 별도로 다룬다 — 이력 보존과 자원 반납이 상충해 판단이 필요하다.
        Surgery saved = surgeryRepository.save(surgery);
        // 취소는 사유가 있는 유일한 전이라 reasonCd 를 함께 남긴다
        recordHistory(surgeryId, StatusChangeType.STATUS, before, SurgeryStatus.CANCELLED, cancelReasonCd);
        return toDto(saved);
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

    /**
     * SL2-225/235/236: 배정 대기 목록 (검색 + 페이지 단위)
     *
     * <p>정렬은 {@link Pageable} 이 들고 온다. 기본값(응급 우선)은 컨트롤러가 정하므로
     * 여기서는 정렬을 신경 쓰지 않는다.</p>
     *
     * <p><b>빈 문자열을 null 로 바꿔 넘기는 이유</b> — 화면 검색창을 비워 보내면
     * {@code patientId=""} 가 온다. 그대로 넘기면 "환자ID 가 빈 문자열인 건"을 찾게 되어
     * 결과가 0건이 된다. 비어 있는 것은 "조건 없음"으로 다뤄야 한다.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<SurgeryDto> getRequestedSchedules(
            String emergencyYn, String patientId, LocalDate fromDt, LocalDate toDt, Pageable pageable) {

        Page<Surgery> result =
                surgeryRepository.searchByStatus(
                        SurgeryStatus.REQUESTED,
                        blankToNull(emergencyYn),
                        blankToNull(patientId),
                        fromDt,
                        toDt,
                        pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    /** 빈 문자열은 "조건 없음"으로 본다. */
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
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
    @Transactional
    public SurgeryDto assignSurgery(String surgeryId, SurgeryDto request) {
        Surgery surgery = findOrThrow(surgeryId);
        requireTransition(surgery.getStatusCd(), SurgeryStatus.SCHEDULED);
        if (request.getRoomCode() == null || request.getRoomCode().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "roomCode 누락");
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
        String before = surgery.getStatusCd();
        surgery.setStatusCd(SurgeryStatus.SCHEDULED);
        Surgery saved = surgeryRepository.save(surgery);
        recordHistory(surgeryId, StatusChangeType.STATUS, before, SurgeryStatus.SCHEDULED, null);
        return toDto(saved);
    }

    /** 수술 시작 — 예약 상태에서만 가능하며 실제 시작일을 남긴다. */
    @Override
    @Transactional
    public SurgeryDto startSurgery(String surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        String before = surgery.getStatusCd();
        requireTransition(before, SurgeryStatus.IN_PROGRESS);
        surgery.setStatusCd(SurgeryStatus.IN_PROGRESS);
        if (surgery.getActualStartDt() == null) {
            // actual_start_dt는 DDL상 DATE(§14.2 `_dt` = 날짜)라 LocalDate를 쓴다.
            surgery.setActualStartDt(LocalDate.now());
        }
        Surgery saved = surgeryRepository.save(surgery);
        recordHistory(surgeryId, StatusChangeType.STATUS, before, SurgeryStatus.IN_PROGRESS, null);
        return toDto(saved);
    }

    @Override
    public List<SurgeryDto> getTodaySchedules() {
        return getSchedules(LocalDate.now());
    }

    @Override
    @Transactional
    public SurgeryDto updateProgress(String surgeryId, String progressCd) {
        Surgery surgery = findOrThrow(surgeryId);

        // SL2-281: 진행단계는 수술이 진행중일 때만 의미가 있다.
        //
        // statusCd 전이표와 따로 검사하는 이유 — progressCd 는 다른 축이라 전이표에 넣을 수 없다.
        // 검사가 없던 동안 완료·취소된 수술의 진행단계를 바꾸는 요청이 200 으로 통과했고,
        // 그 결과 끝난 수술에 PROGRESS 이력이 계속 쌓일 수 있었다.
        if (!SurgeryStatus.IN_PROGRESS.equals(surgery.getStatusCd())) {
            throw new BusinessException(
                    ErrorCode.INVALID_SURGERY_STATUS, "진행단계 변경 시도 상태=" + surgery.getStatusCd());
        }

        String before = surgery.getProgressCd();
        surgery.setProgressCd(progressCd);
        Surgery saved = surgeryRepository.save(surgery);
        // 여기만 PROGRESS 다 — 나머지 전이는 전부 STATUS 다. 헷갈리면 조회할 때 두 종류가 섞인다.
        recordHistory(surgeryId, StatusChangeType.PROGRESS, before, progressCd, null);
        return toDto(saved);
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
    @Transactional
    public SurgeryDto completeSurgery(String surgeryId) {
        Surgery surgery = findOrThrow(surgeryId);
        String before = surgery.getStatusCd();
        // SL2-281: 검사가 없던 동안 취소·완료된 수술도 완료 처리가 200 으로 통과했다.
        requireTransition(before, SurgeryStatus.COMPLETED);
        surgery.setStatusCd(SurgeryStatus.COMPLETED);
        if (surgery.getActualEndDt() == null) {
            // actual_end_dt는 DDL상 DATE(§14.2 `_dt` = 날짜)라 LocalDate를 쓴다.
            surgery.setActualEndDt(LocalDate.now());
        }
        Surgery saved = surgeryRepository.save(surgery);
        recordHistory(surgeryId, StatusChangeType.STATUS, before, SurgeryStatus.COMPLETED, null);
        return toDto(saved);
    }

    private Surgery findOrThrow(String surgeryId) {
        return surgeryRepository
                .findById(surgeryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SURGERY_NOT_FOUND, surgeryId));
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
                s.getSurgeryTypeCd(),
                s.getSurgeryName(),
                s.getEmergencyYn(),
                s.getActualStartDt(),
                s.getActualEndDt(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
