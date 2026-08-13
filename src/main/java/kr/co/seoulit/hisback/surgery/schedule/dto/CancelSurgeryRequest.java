package kr.co.seoulit.hisback.surgery.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 취소·반려 요청 (SL2-33 취소 / SL2-227 반려 사유 입력)
 *
 * <p><b>왜 Map 대신 DTO 인가</b> — 이 API 는 지금까지 {@code Map<String, String>} 을 받아
 * {@code request.get("cancelReasonCd")} 로 값을 꺼냈다. 그러면 세 가지가 안 된다.
 * 키를 잘못 적어도(cancelReason, reasonCd 등) 아무 오류 없이 null 이 되고,
 * {@code @Valid} 를 걸 수 없어 검증이 불가능하며, Swagger 에 어떤 필드를 보내야 하는지
 * 드러나지 않는다.</p>
 *
 * <p><b>사유를 필수로 두지 않은 이유</b> — 같은 엔드포인트가 두 업무를 겸한다.
 * 요청접수(00)에서의 취소는 업무상 '반려'라 사유가 있어야 하지만, 예약(01) 상태의
 * 취소는 환자 사정 등으로 사유 없이 일어나기도 한다. 상태에 따라 필수 여부를 가르는
 * 것은 서비스가 판단한다(§11.5 — 형식 검증은 DTO, 업무 규칙은 서비스).</p>
 *
 * <p>값의 유효성은 admin 의 {@code SURGERY_CANCEL_CD} 그룹으로 검증한다. 그룹이 아직
 * 등록되지 않았다면 검증을 건너뛴다 — 수술실 상태코드에서 쓴 것과 같은 판단이다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelSurgeryRequest {

    /**
     * 취소·반려 사유 코드 (SURGERY_CANCEL_CD).
     *
     * <p>{@code _cd} 접미사대로 코드값만 담는다(§14.2). 사유를 자유 문구로 받지 않는 이유는
     * 통계를 낼 수 없기 때문이다 — "환자 거부"와 "환자거부"가 다른 사유로 집계된다.</p>
     */
    private String cancelReasonCd;
}
