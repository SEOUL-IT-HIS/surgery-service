package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 반려 요청 (수술실 담당자가 보낸다)
 *
 * <p>반려는 되돌릴 수 없다. 진료가 다시 요청하면 <b>새 오더</b>가 생기고, 앞 오더는
 * 반려된 채로 남는다 — 그래야 "몇 번 요청했고 왜 반려됐는지"가 추적된다.</p>
 *
 * <p><b>사유를 필수로 바꿨다</b>. 예전에는 선택이었는데 이유는 하나였다 —
 * 사유 코드 그룹({@code SURGERY_ORDER_REJECT_CD})이 admin 에 없어 필수로 두면 고를 값이
 * 없었다. 2026-08-25 에 등록했으므로(01 환자 일정 지연 / 02 서류 미충족 / 03 수술 전 사망 /
 * 04 수술실 사정 / 05 기타) 그 이유가 사라졌다.</p>
 *
 * <p>수술 취소({@code CancelSurgeryRequest})와 같은 판단이다 — 되돌릴 수 없는 처리에는
 * 왜 그랬는지가 남아야 한다. 사유 없이 반려된 요청은 진료가 다시 물어올 때 답할 수 없다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectSurgeryOrderRequest {

    /**
     * 반려 사유 코드 (SURGERY_ORDER_REJECT_CD). <b>필수</b>다.
     *
     * <p>{@code @NotBlank} 는 빈 문자열과 공백만 있는 값도 막는다 — 화면이 고르지 않은
     * 셀렉트를 {@code ""} 로 보내는 일이 흔해서 {@code @NotNull} 로는 부족하다.</p>
     */
    @NotBlank
    private String rejectReasonCd;
}
