package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 배정 요청 (수술실 담당자가 보낸다)
 *
 * <p><b>행위는 '배정'이고, 오더의 '수락'은 그 결과다.</b> 담당자가 하는 일은 수술실을
 * 정하는 것이지 "이 요청을 받아들이겠다"고 선언하는 것이 아니다. 수술실이 정해지는
 * 순간 그 요청은 받아들여진 것이므로, 오더 상태가 수락(01)으로 따라 바뀐다.</p>
 *
 * <p>그래서 이 DTO 이름도 Accept 가 아니라 Assign 이다. 엔드포인트도
 * {@code PATCH /orders/{orderId}/assign} 이다.</p>
 *
 * <h3>배정은 여기서 한 번에 끝난다</h3>
 *
 * <p>예전에는 수술실만 필수였다 — "마취의·간호사는 수술 당일까지 채워도 되는 항목"이라는
 * 이유였고, 나중에 개별 배정 API 로 채우면 된다고 봤다.</p>
 *
 * <p>그 방식을 접었다. 개별 배정 API 는 이력을 남기지 않아 누가 언제 바꿨는지 알 수
 * 없었고, 무엇보다 <b>배정이 끝난 건지 아닌지를 아무도 알 수 없었다</b> — 수술실만
 * 잡힌 건과 팀까지 다 잡힌 건이 화면에서 똑같이 "예약"으로 보였다. 승인 담당자가
 * 그 자리에서 전부 확인하고 넘기는 편이 낫다는 결론이다.</p>
 *
 * <p>그래서 지금은 <b>승인하는 순간 배정이 확정</b>되고, 그 뒤로는 바꿀 수 없다
 * (개별 배정 API 는 SUR059 로 거절한다). 잘못 배정했으면 수술을 취소하고 다시
 * 요청받는다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignSurgeryOrderRequest {

    /** 배정할 수술실. 실재하고 사용가능(01) 상태여야 한다 */
    @NotBlank
    private String roomCode;

    /**
     * 확정 수술일. 비우면 오더의 희망일을 그대로 쓴다.
     *
     * <p>수술실 사정으로 날짜가 바뀌는 일이 흔해서 여기서 조정할 수 있게 열어 뒀다.
     * 오더의 {@code requestedDt} 는 "진료가 원래 원했던 날"로 그대로 남는다.</p>
     */
    private LocalDate surgeryDt;

    /**
     * 마취 시행 여부 (Y/N). 필수다.
     *
     * <p>기본값을 두지 않고 매번 고르게 하는 이유 — 이 값이 마취의 필수 여부를 정한다.
     * 안 보내면 서버가 알아서 Y 로 봐 주는 식으로 두면, 무마취 시술을 배정할 때마다
     * 마취의를 찾다가 막히고 그 이유를 알기 어렵다.</p>
     *
     * <p>N 은 마취과가 붙지 않는 시술이다 — 단순 봉합, 표재성 종물 제거 같은 것들.
     * 국소마취를 수술 팀이 직접 하는 경우도 여기 들어간다.</p>
     */
    @NotBlank
    @Pattern(regexp = "[YN]", message = "마취 여부는 Y 또는 N 이어야 합니다")
    private String anesthesiaYn;

    /**
     * 마취의. {@code anesthesiaYn='Y'} 일 때 필수다.
     *
     * <p>여기에 {@code @NotBlank} 를 걸지 않은 이유 — 필수 여부가 다른 필드 값에 달려
     * 있어 형식 검증(§11.5)으로 표현할 수 없다. 업무 규칙이므로 서비스가 본다
     * ({@code SurgeryOrderServiceImpl.assignOrder}).</p>
     */
    private String anesthesiologistId;

    /**
     * 간호사. 필수다.
     *
     * <p>마취 여부와 무관하게 수술에는 간호사가 붙는다. 무마취 시술이라도 기구·거즈
     * 수량 확인(체크리스트 Sign Out)을 할 사람이 있어야 한다.</p>
     */
    @NotBlank
    private String nurseId;
}
