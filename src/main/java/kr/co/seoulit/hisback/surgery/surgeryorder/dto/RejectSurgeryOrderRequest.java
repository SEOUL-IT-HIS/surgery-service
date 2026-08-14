package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 반려 요청 (수술실 담당자가 보낸다)
 *
 * <p>반려는 되돌릴 수 없다. 진료가 다시 요청하면 <b>새 오더</b>가 생기고, 앞 오더는
 * 반려된 채로 남는다 — 그래야 "몇 번 요청했고 왜 반려됐는지"가 추적된다.</p>
 *
 * <p><b>사유를 필수로 두지 않은 이유</b> — 사유 코드 그룹(SURGERY_ORDER_REJECT_CD)이
 * admin 에 아직 없다. 코드가 등록되기 전이라고 반려 업무를 막을 수는 없어 비워 두고도
 * 반려할 수 있게 했다. 그룹이 생기면 값 검증이 저절로 살아난다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectSurgeryOrderRequest {

    /** 반려 사유 코드 (SURGERY_ORDER_REJECT_CD). 코드 등록 전까지는 비워도 된다 */
    private String rejectReasonCd;
}
