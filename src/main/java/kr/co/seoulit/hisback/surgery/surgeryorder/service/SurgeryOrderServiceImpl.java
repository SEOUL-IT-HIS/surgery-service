package kr.co.seoulit.hisback.surgery.surgeryorder.service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.common.cache.CommonCodeCache;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.room.entity.OperatingRoom;
import kr.co.seoulit.hisback.surgery.room.repository.OperatingRoomRepository;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryScheduleService;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.AssignSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.CreateSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.RejectSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.SurgeryOrderDto;
import kr.co.seoulit.hisback.surgery.surgeryorder.entity.SurgeryOrder;
import kr.co.seoulit.hisback.surgery.surgeryorder.repository.SurgeryOrderRepository;
import kr.co.seoulit.hisback.surgery.surgeryorder.type.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술 오더 서비스 구현체
 *
 * <p>오더는 진료·응급실이 보낸 <b>요청</b>이고, 수술은 우리가 받아들여 만든 <b>일정</b>이다.
 * 이 클래스는 요청을 받아 처리하는 데까지만 책임지고, 수술이 어떻게 진행되는지는
 * SurgeryScheduleService 가 맡는다.</p>
 */
@Service
@Transactional(readOnly = true)
public class SurgeryOrderServiceImpl implements SurgeryOrderService {

    /** OR_STATUS_CD 01 = 사용가능. 배정 가능한 수술실인지 판단하는 기준이다 */
    private static final String ROOM_STATUS_AVAILABLE = "01";

    /**
     * 반려 사유 코드 그룹.
     *
     * <p>admin 에 아직 등록되지 않았다. 등록 전까지는 검증을 건너뛴다 — 코드가 없다고
     * 반려 업무를 막을 수는 없다. 그룹이 생기면 검증이 저절로 살아난다.</p>
     */
    private static final String GROUP_REJECT_REASON = "SURGERY_ORDER_REJECT_CD";

    private final SurgeryOrderRepository surgeryOrderRepository;
    private final OperatingRoomRepository operatingRoomRepository;
    private final SurgeryScheduleService surgeryScheduleService;
    private final CommonCodeCache commonCodeCache;

    public SurgeryOrderServiceImpl(
            SurgeryOrderRepository surgeryOrderRepository,
            OperatingRoomRepository operatingRoomRepository,
            SurgeryScheduleService surgeryScheduleService,
            CommonCodeCache commonCodeCache) {
        this.surgeryOrderRepository = surgeryOrderRepository;
        this.operatingRoomRepository = operatingRoomRepository;
        this.surgeryScheduleService = surgeryScheduleService;
        this.commonCodeCache = commonCodeCache;
    }

    @Override
    public PageResponse<SurgeryOrderDto> getOrders(
            String orderStatusCd,
            String emergencyYn,
            String patientId,
            LocalDate fromDt,
            LocalDate toDt,
            Pageable pageable) {

        Page<SurgeryOrder> result =
                surgeryOrderRepository.search(
                        blankToNull(orderStatusCd),
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

    @Override
    public SurgeryOrderDto getOrder(String orderId) {
        return toDto(findOrThrow(orderId));
    }

    /**
     * 오더 접수 (SL2-36 / SL2-44)
     *
     * <p>상태는 항상 접수(00)로 시작한다. 응급 여부는 인자로 받은 값만 쓴다 —
     * 요청 본문에는 그 필드가 아예 없어서 클라이언트가 실어 보낼 수 없다.</p>
     */
    @Override
    @Transactional
    public SurgeryOrderDto createOrder(CreateSurgeryOrderRequest request, boolean emergency) {
        SurgeryOrder order =
                SurgeryOrder.builder()
                        .orderId(UUID.randomUUID().toString())
                        .patientId(request.getPatientId())
                        .visitId(blankToNull(request.getVisitId()))
                        .surgeonId(request.getSurgeonId())
                        .requestedDt(request.getRequestedDt())
                        .emergencyYn(emergency ? "Y" : "N")
                        .orderStatusCd(OrderStatus.RECEIVED)
                        .surgeryTypeCd(blankToNull(request.getSurgeryTypeCd()))
                        .surgeryName(blankToNull(request.getSurgeryName()))
                        .orderedBy(blankToNull(request.getOrderedBy()))
                        .build();

        return toDto(surgeryOrderRepository.save(order));
    }

    /**
     * 수술실 배정 → 오더 수락 (SL2-15)
     *
     * <p>순서가 중요하다. 수술실을 먼저 검증하고, 수술을 만든 뒤, 오더에 그 식별자를 적는다.
     * 수술 생성이 실패하면 오더는 접수 상태 그대로 남아야 한다 — 한 트랜잭션이라
     * 중간에 끊겨도 "수락됐는데 수술이 없는" 상태가 생기지 않는다.</p>
     */
    @Override
    @Transactional
    public SurgeryOrderDto assignOrder(String orderId, AssignSurgeryOrderRequest request) {
        SurgeryOrder order = findOrThrow(orderId);
        requireReceived(order);
        requireRoomAssignable(request.getRoomCode());

        // 확정일을 안 보내면 진료가 원한 날을 그대로 쓴다
        LocalDate surgeryDt =
                (request.getSurgeryDt() != null) ? request.getSurgeryDt() : order.getRequestedDt();

        SurgeryDto toCreate = new SurgeryDto();
        toCreate.setPatientId(order.getPatientId());
        toCreate.setSurgeonId(order.getSurgeonId());
        toCreate.setSurgeryDt(surgeryDt);
        toCreate.setRoomCode(request.getRoomCode());
        toCreate.setAnesthesiologistId(blankToNull(request.getAnesthesiologistId()));
        toCreate.setNurseId(blankToNull(request.getNurseId()));
        toCreate.setSurgeryTypeCd(order.getSurgeryTypeCd());
        toCreate.setSurgeryName(order.getSurgeryName());
        toCreate.setEmergencyYn(order.getEmergencyYn());

        SurgeryDto created = surgeryScheduleService.createScheduledSurgery(toCreate);

        order.setSurgeryId(created.getSurgeryId());
        order.setOrderStatusCd(OrderStatus.ACCEPTED);

        return toDto(surgeryOrderRepository.save(order));
    }

    /**
     * 오더 반려 (SL2-226)
     *
     * <p>SURGERY 는 만들지 않는다. 사유 코드가 왔다면 admin 에 등록된 값인지 확인하되,
     * 그룹이 아직 없으면 건너뛴다.</p>
     */
    @Override
    @Transactional
    public SurgeryOrderDto rejectOrder(String orderId, RejectSurgeryOrderRequest request) {
        SurgeryOrder order = findOrThrow(orderId);
        requireReceived(order);

        String reasonCd = (request != null) ? blankToNull(request.getRejectReasonCd()) : null;
        if (reasonCd != null
                && commonCodeCache.hasGroup(GROUP_REJECT_REASON)
                && !commonCodeCache.isValid(GROUP_REJECT_REASON, reasonCd)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST, GROUP_REJECT_REASON + "=" + reasonCd);
        }

        order.setOrderStatusCd(OrderStatus.REJECTED);
        order.setRejectReasonCd(reasonCd);

        return toDto(surgeryOrderRepository.save(order));
    }

    /** 없으면 404 SUR057. */
    private SurgeryOrder findOrThrow(String orderId) {
        return surgeryOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, orderId));
    }

    /**
     * 접수(00) 상태에서만 배정·반려할 수 있다.
     *
     * <p>이미 수락된 오더를 다시 배정하면 수술이 두 건 생기고, 반려된 오더를 배정하면
     * 반려 사실이 지워진다. 둘 다 400 SUR058 로 막는다.</p>
     */
    private void requireReceived(SurgeryOrder order) {
        if (!OrderStatus.RECEIVED.equals(order.getOrderStatusCd())) {
            throw new BusinessException(
                    ErrorCode.INVALID_ORDER_STATUS, "오더 상태=" + order.getOrderStatusCd());
        }
    }

    /**
     * 수술실이 실재하고 배정 가능한 상태인지 확인한다 (SL2-169 와 같은 규칙).
     *
     * <p>장비 충족·시간 충돌 검증은 아직 못 한다 — 수술이 필요 장비를 선언하는 자리가 없고,
     * 날짜만 있고 시각이 없어 같은 날 같은 방에 두 건이 잡혀도 겹치는지 알 수 없다.</p>
     */
    private void requireRoomAssignable(String roomCode) {
        OperatingRoom room =
                operatingRoomRepository
                        .findById(roomCode)
                        .orElseThrow(
                                () -> new BusinessException(ErrorCode.SURGERY_ROOM_NOT_FOUND, roomCode));

        if (!ROOM_STATUS_AVAILABLE.equals(room.getStatusCd())) {
            throw new BusinessException(
                    ErrorCode.SURGERY_ROOM_NOT_AVAILABLE, roomCode + " 상태=" + room.getStatusCd());
        }
    }

    /** 빈 문자열은 "값 없음"으로 본다 — 화면이 빈 칸을 보내도 조건이나 저장값이 어긋나지 않게 한다. */
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    /** 엔티티 → DTO. 필드 순서는 DTO 선언 순서와 같아야 한다(@AllArgsConstructor 는 순서로 받는다). */
    private SurgeryOrderDto toDto(SurgeryOrder o) {
        return new SurgeryOrderDto(
                o.getOrderId(),
                o.getPatientId(),
                o.getVisitId(),
                o.getSurgeonId(),
                o.getRequestedDt(),
                o.getEmergencyYn(),
                o.getOrderStatusCd(),
                o.getRejectReasonCd(),
                o.getSurgeryTypeCd(),
                o.getSurgeryName(),
                o.getOrderedBy(),
                o.getSurgeryId(),
                o.getCreatedAt(),
                o.getUpdatedAt());
    }
}
