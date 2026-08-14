package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 배정 요청 (수술실 담당자가 보낸다)
 *
 * <p><b>행위는 '배정'이고, 오더의 '수락'은 그 결과다.</b> 담당자가 하는 일은 수술실을
 * 정하는 것이지 "이 요청을 받아들이겠다"고 선언하는 것이 아니다. 수술실이 정해지는
 * 순간 그 요청은 받아들여진 것이므로, 오더 상태가 수락(01)으로 따라 바뀐다.
 * (2026-08-13 결정)</p>
 *
 * <p>그래서 이 DTO 이름도 Accept 가 아니라 Assign 이다. 엔드포인트도
 * {@code PATCH /orders/{orderId}/assign} 이다.</p>
 *
 * <p><b>수술실만 필수인 이유</b> — 배정 완료의 기준을 "수술실이 정해졌는가"로 잡았다.
 * 마취의·간호사는 수술 당일까지 채워도 되는 항목이라, 이것들을 기다리면 수술실이
 * 잡혔는데도 오더가 접수 상태로 남는다.</p>
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

    /** 마취의. 나중에 채워도 되므로 선택이다 */
    private String anesthesiologistId;

    /** 간호사. 나중에 채워도 되므로 선택이다 */
    private String nurseId;
}
