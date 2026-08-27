package kr.co.seoulit.hisback.surgery.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 취소 요청 (SL2-33 취소 / SL2-178 사유 필수 / SL2-227 사유 코드 검증)
 *
 * <p><b>왜 Map 대신 DTO 인가</b> — 이 API 는 지금까지 {@code Map<String, String>} 을 받아
 * {@code request.get("cancelReasonCd")} 로 값을 꺼냈다. 그러면 세 가지가 안 된다.
 * 키를 잘못 적어도(cancelReason, reasonCd 등) 아무 오류 없이 null 이 되고,
 * {@code @Valid} 를 걸 수 없어 검증이 불가능하며, Swagger 에 어떤 필드를 보내야 하는지
 * 드러나지 않는다.</p>
 *
 * <p><b>사유를 필수로 바꿨다</b>(2026-08-26, SL2-178). 예전에는 선택이었고 근거는 두 가지였다 —
 * 같은 엔드포인트가 취소와 반려를 겸했고, {@code SURGERY_CANCEL_CD} 그룹이 admin 에 없어
 * 필수로 두면 고를 값이 없었다. <b>둘 다 사라졌다</b>: 반려는 오더로 옮겨갔고
 * ({@code PATCH /api/surgery/orders/{orderId}/reject}, 2026-08-13), 코드그룹도 등록했다(08-25).</p>
 *
 * <p>이제 이 엔드포인트는 <b>이미 만들어진 수술의 취소</b> 하나만 다룬다. 취소는 되돌릴 수
 * 없는 전이라 왜 취소했는지가 남아야 하고, 사유 없이 사라진 수술은 나중에 설명할 수 없다.</p>
 *
 * <p>값의 유효성은 admin 의 {@code SURGERY_CANCEL_CD} 그룹으로 검증한다(2026-08-25 등록 완료).
 * 그룹이 사라지거나 캐시가 아직 안 돌았을 때만 검증을 건너뛴다 — 그때 막아버리면
 * 멀쩡한 취소 업무까지 멈춘다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelSurgeryRequest {

    /**
     * 취소 사유 코드 (SURGERY_CANCEL_CD). <b>필수</b>다.
     *
     * <p>{@code _cd} 접미사대로 코드값만 담는다(§14.2). 사유를 자유 문구로 받지 않는 이유는
     * 통계를 낼 수 없기 때문이다 — "환자 거부"와 "환자거부"가 다른 사유로 집계된다.</p>
     *
     * <p>{@code @NotBlank} 는 빈 문자열과 공백만 있는 값도 막는다. 화면이 고르지 않은 셀렉트를
     * {@code ""} 로 보내는 일이 흔해서 {@code @NotNull} 로는 부족하다.</p>
     */
    @NotBlank
    private String cancelReasonCd;
}
