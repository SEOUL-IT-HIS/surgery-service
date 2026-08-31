package kr.co.seoulit.hisback.surgery.surgeryorder.service;

import kr.co.seoulit.hisback.surgery.surgeryorder.entity.SurgeryOrder;
import kr.co.seoulit.hisback.surgery.surgeryorder.repository.SurgeryOrderRepository;
import kr.co.seoulit.hisback.surgery.surgeryorder.type.OrderStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술이 취소됐을 때 그 사실을 오더에 반영한다 (SL2-179)
 *
 * <h3>왜 필요한가</h3>
 *
 * <p>수락(01)된 오더는 {@code surgery_id} 로 수술을 가리킨다. 그런데 그 수술이 취소돼도
 * 오더는 계속 "수락됨"이라고 말한다. 진료·응급 입장에서는 자기 요청이 여전히 받아들여진
 * 상태로 보이고, 수술이 무산된 것을 알 길이 없다. 오더 상태의 소유자는 수술 서비스인데
 * (§21.2) 갱신을 하지 않아 상태가 사실과 어긋나 있었다.</p>
 *
 * <h3>왜 별도 클래스인가 — 순환참조</h3>
 *
 * <p>{@link SurgeryOrderServiceImpl} 은 이미 {@code SurgeryScheduleService} 를 주입받는다
 * (오더를 배정할 때 수술을 만들어야 해서다). 그래서 반대로 스케줄 서비스가
 * {@code SurgeryOrderService} 를 주입받으면 생성자 순환참조가 되어 애플리케이션이 아예
 * 뜨지 않는다.</p>
 *
 * <p>이 클래스는 <b>리포지토리 하나에만</b> 의존한다. 수술 쪽을 전혀 모르므로 스케줄
 * 서비스가 안전하게 주입받을 수 있다. {@code @Lazy} 로 순환을 눌러 덮는 방법도 있지만,
 * 그건 문제를 감추는 것이지 없애는 것이 아니다 — 의존 방향이 여전히 양쪽이라 다음 사람이
 * 같은 함정에 빠진다.</p>
 *
 * <h3>배정 정보를 지우지 않는 이유</h3>
 *
 * <p>SL2-179 의 문구는 "집도의·간호사·마취의·수술실 배정 일괄 해제"지만, 실제로 필드를
 * null 로 미는 방식은 택하지 않았다.</p>
 *
 * <ul>
 *   <li><b>자원이 묶이지 않는다</b> — 해제의 목적은 취소된 건이 수술실을 점유한 것처럼
 *       보이지 않게 하는 것인데, 모니터링은 이미 취소를 걸러낸다. {@code inUse} 는 진행중만
 *       보고, {@code scheduledCount} 와 미배정 집계는 취소를 제외한다.</li>
 *   <li><b>집도의는 애초에 비울 수 없다</b> — {@code surgeon_id} 가 NOT NULL 이고,
 *       "수술에 집도의가 없는 상태는 업무상 성립하지 않는다"고 이미 정해 두었다.
 *       문구대로 하려면 이 제약을 풀어야 하는데, 그러면 취소가 아닌 정상 수술에서도
 *       집도의 없는 행이 생길 길이 열린다.</li>
 *   <li><b>기록이 사라진다</b> — 지우고 나면 "그 수술은 몇 번 방에 누가 잡혀 있었나"를
 *       영영 알 수 없다. §21.6 이 삭제보다 상태 변경을 권하는 것과 같은 취지다.</li>
 * </ul>
 *
 * <p>정작 되지 않고 있던 것은 <b>요청자가 결과를 모른다</b>는 쪽이었고, 오더 상태를
 * 취소(03)로 바꾸면 그것이 풀린다. 배정 필드는 그대로 두므로 해제 전 값을 어딘가에
 * 보존할 필요도 없다.</p>
 */
@Component
public class SurgeryOrderCanceller {

    private final SurgeryOrderRepository surgeryOrderRepository;

    public SurgeryOrderCanceller(SurgeryOrderRepository surgeryOrderRepository) {
        this.surgeryOrderRepository = surgeryOrderRepository;
    }

    /**
     * 이 수술에서 비롯된 오더가 있으면 취소(03)로 바꾼다.
     *
     * <p>호출하는 쪽(수술 취소)의 트랜잭션에 참여한다 — 수술은 취소됐는데 오더는 그대로인
     * 어긋난 상태가 남지 않게 하려면 같이 커밋되거나 같이 롤백돼야 한다.</p>
     *
     * <p><b>오더가 없어도 오류가 아니다.</b> 오더 없이 만들어진 수술이 있을 수 있고,
     * 무엇보다 수술 취소가 오더 유무 때문에 실패해서는 안 된다.</p>
     *
     * <p><b>수락(01)이 아니면 건드리지 않는다.</b> 반려(02)나 이미 취소(03)된 오더를 다시
     * 덮으면 반려 사실이 지워지거나 갱신 시각만 어지럽게 바뀐다.</p>
     *
     * <p>취소 사유는 여기서 저장하지 않는다 — SURGERY 가 갖고 있고 오더는
     * {@code surgery_id} 로 닿을 수 있다. 두 곳에 두면 정정 시 어긋난다.</p>
     *
     * @param surgeryId 취소된 수술의 식별자
     * @return 실제로 오더를 바꿨으면 true
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    public boolean cancelBySurgery(String surgeryId) {
        SurgeryOrder order = surgeryOrderRepository.findBySurgeryId(surgeryId).orElse(null);
        if (order == null || !OrderStatus.ACCEPTED.equals(order.getOrderStatusCd())) {
            return false;
        }
        order.setOrderStatusCd(OrderStatus.CANCELLED);
        surgeryOrderRepository.save(order);
        return true;
    }
}
