package kr.co.seoulit.hisback.surgery.surgeryorder.service;

import java.time.LocalDate;
import kr.co.seoulit.hisback.surgery.common.response.PageResponse;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.AssignSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.CreateSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.RejectSurgeryOrderRequest;
import kr.co.seoulit.hisback.surgery.surgeryorder.dto.SurgeryOrderDto;
import org.springframework.data.domain.Pageable;

/**
 * 수술 오더 서비스 (SL2-36 진료 요청 / SL2-44 응급 요청 / SL2-225 목록 / SL2-226 반려)
 *
 * <p>진료·응급실이 보낸 <b>요청</b>을 다룬다. 수술 자체의 진행은 SurgeryScheduleService 소관이다.</p>
 *
 * <p>두 서비스가 만나는 지점은 하나뿐이다 — 배정이 끝나 오더가 수락될 때 SURGERY 가
 * 만들어진다. 그 생성은 schedule 쪽에 맡긴다(이력 기록이 거기 있기 때문이다).</p>
 */
public interface SurgeryOrderService {

    /**
     * 오더 목록 조회 (SL2-225)
     *
     * <p>조건은 모두 선택이다. 아무것도 안 주면 전체가 나온다 — 반려된 오더도 포함된다.
     * 배정 대기만 보려면 {@code orderStatusCd=00} 을 준다.</p>
     */
    PageResponse<SurgeryOrderDto> getOrders(
            String orderStatusCd,
            String emergencyYn,
            String patientId,
            LocalDate fromDt,
            LocalDate toDt,
            Pageable pageable);

    /** 오더 단건 조회. 없으면 404 SUR057. */
    SurgeryOrderDto getOrder(String orderId);

    /**
     * 오더 접수 (SL2-36 진료 / SL2-44 응급)
     *
     * <p>응급 여부를 <b>인자로 받는다</b> — 요청 본문이 아니라 어느 엔드포인트로 들어왔는지가
     * 정한다. 일반 요청이 스스로 'Y' 를 실어 배정 우선순위를 가로채지 못하게 하기 위해서다.</p>
     */
    SurgeryOrderDto createOrder(CreateSurgeryOrderRequest request, boolean emergency);

    /**
     * 수술실 배정 → 오더 수락 (SL2-15)
     *
     * <p>수술실이 정해지는 순간 요청이 받아들여진 것이므로, SURGERY 를 만들고 오더를
     * 수락(01)으로 바꾼다. 두 저장이 한 트랜잭션이라 한쪽만 남는 일이 없다.</p>
     *
     * <p>접수(00) 상태에서만 가능하다. 이미 수락·반려된 오더는 400 SUR058.</p>
     */
    SurgeryOrderDto assignOrder(String orderId, AssignSurgeryOrderRequest request);

    /**
     * 오더 반려 (SL2-226)
     *
     * <p>SURGERY 를 만들지 않는다 — 한 번도 수술이 아니었던 것을 수술 통계에 남기지 않기
     * 위해서다. 진료가 다시 요청하면 새 오더가 생기고 이 오더는 반려된 채로 남는다.</p>
     */
    SurgeryOrderDto rejectOrder(String orderId, RejectSurgeryOrderRequest request);
}
