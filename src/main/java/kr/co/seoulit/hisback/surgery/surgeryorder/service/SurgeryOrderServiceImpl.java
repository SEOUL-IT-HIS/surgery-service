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
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
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
     * <p>2026-08-25 admin 에 등록했다 — 01 환자 일정 지연 / 02 서류 미충족 /
     * 03 수술 전 사망 / 04 수술실 사정 / 05 기타. 이제 검증이 실제로 걸린다.</p>
     *
     * <p>{@code hasGroup} 로 한 번 거르는 구조는 그대로 둔다 — 그룹이 사라지거나 캐시가
     * 아직 안 돌았을 때 반려 업무 자체가 멈추는 것을 막는다.</p>
     */
    private static final String GROUP_REJECT_REASON = "SURGERY_ORDER_REJECT_CD";

    private final SurgeryOrderRepository surgeryOrderRepository;
    private final OperatingRoomRepository operatingRoomRepository;
    private final SurgeryScheduleService surgeryScheduleService;
    private final CommonCodeCache commonCodeCache;

    /**
     * 취소 사유를 읽기 위해서만 쓴다 — <b>읽기 전용</b>이다.
     *
     * <p>수술을 만들거나 고치는 일은 {@link SurgeryScheduleService} 를 거친다. 여기서
     * 리포지토리를 직접 잡은 이유는 사유 한 컬럼을 읽자고 서비스 메서드를 늘리고 싶지
     * 않아서이고, 같은 이유로 {@code OperatingRoomRepository} 도 직접 쓰고 있다.</p>
     */
    private final SurgeryRepository surgeryRepository;

    public SurgeryOrderServiceImpl(
            SurgeryOrderRepository surgeryOrderRepository,
            OperatingRoomRepository operatingRoomRepository,
            SurgeryScheduleService surgeryScheduleService,
            CommonCodeCache commonCodeCache,
            SurgeryRepository surgeryRepository) {
        this.surgeryOrderRepository = surgeryOrderRepository;
        this.operatingRoomRepository = operatingRoomRepository;
        this.surgeryScheduleService = surgeryScheduleService;
        this.commonCodeCache = commonCodeCache;
        this.surgeryRepository = surgeryRepository;
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
                toDto(result.getContent()),
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

        // 사유는 필수다(2026-08-26). 컨트롤러의 @Valid 가 먼저 막지만, 서비스를 직접 부르는
        //   경로(테스트·다른 서비스)도 있으므로 여기서도 확인한다(§11.5 업무 규칙은 서비스).
        String reasonCd = (request != null) ? blankToNull(request.getRejectReasonCd()) : null;
        if (reasonCd == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "반려 사유는 필수입니다");
        }

        if (commonCodeCache.hasGroup(GROUP_REJECT_REASON)
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

    /**
     * 취소된 오더 하나의 취소 사유를 수술에서 읽는다.
     *
     * <p>취소(03)가 아니거나 연결된 수술이 없으면 null 이다. 목록에서는 이 메서드를 쓰지
     * 말 것 — 행마다 한 번씩 조회하게 된다({@link #toDto(java.util.List)} 참고).</p>
     */
    private String readCancelReason(SurgeryOrder o) {
        if (!OrderStatus.CANCELLED.equals(o.getOrderStatusCd()) || o.getSurgeryId() == null) {
            return null;
        }
        return surgeryRepository
                .findById(o.getSurgeryId())
                .map(Surgery::getCancelReasonCd)
                .orElse(null);
    }

    /**
     * 목록 변환 — 취소 사유를 <b>한 번에</b> 읽는다.
     *
     * <p>행마다 {@code findById} 를 부르면 N+1 이 된다. 취소 상태로 걸러 조회한 목록이면
     * 모든 행이 취소라 그 부담이 그대로 드러난다. 취소된 행의 surgeryId 만 모아
     * {@code findAllById} 로 한 번 읽고 맵으로 맞춘다.</p>
     *
     * <p>취소 건이 하나도 없으면 추가 조회 자체를 하지 않는다 — 대부분의 목록이 그렇다.</p>
     */
    private java.util.List<SurgeryOrderDto> toDto(java.util.List<SurgeryOrder> orders) {
        java.util.List<String> cancelledSurgeryIds =
                orders.stream()
                        .filter(o -> OrderStatus.CANCELLED.equals(o.getOrderStatusCd()))
                        .map(SurgeryOrder::getSurgeryId)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());

        java.util.Map<String, String> reasonBySurgeryId =
                cancelledSurgeryIds.isEmpty()
                        ? java.util.Map.of()
                        : surgeryRepository.findAllById(cancelledSurgeryIds).stream()
                                .filter(s -> s.getCancelReasonCd() != null)
                                .collect(
                                        Collectors.toMap(Surgery::getSurgeryId, Surgery::getCancelReasonCd));

        return orders.stream()
                .map(
                        o ->
                                toDto(
                                        o,
                                        o.getSurgeryId() == null
                                                ? null
                                                : reasonBySurgeryId.get(o.getSurgeryId())))
                .collect(Collectors.toList());
    }

    /** 단건 변환 — 취소 사유까지 채운다. */
    private SurgeryOrderDto toDto(SurgeryOrder o) {
        return toDto(o, readCancelReason(o));
    }

    /** 엔티티 → DTO. 필드 순서는 DTO 선언 순서와 같아야 한다(@AllArgsConstructor 는 순서로 받는다). */
    private SurgeryOrderDto toDto(SurgeryOrder o, String cancelReasonCd) {
        return new SurgeryOrderDto(
                o.getOrderId(),
                o.getPatientId(),
                o.getVisitId(),
                o.getSurgeonId(),
                o.getRequestedDt(),
                o.getEmergencyYn(),
                o.getOrderStatusCd(),
                o.getRejectReasonCd(),
                cancelReasonCd,
                o.getSurgeryTypeCd(),
                o.getSurgeryName(),
                o.getOrderedBy(),
                o.getSurgeryId(),
                o.getCreatedAt(),
                o.getUpdatedAt());
    }
}
