package kr.co.seoulit.hisback.surgery.surgeryorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 오더 접수 요청 (진료·응급실이 보낸다)
 *
 * <p>받는 것은 <b>요청 내용뿐</b>이다. 오더 식별자·상태·응급여부는 서버가 정한다 —
 * 응급여부를 클라이언트가 정하게 두면 일반 요청이 'Y' 를 실어 배정 우선순위를 가로챈다.
 * 그래서 이 DTO 에는 그 필드가 아예 없다(응답 DTO 와 나눈 이유다).</p>
 *
 * <p>응급 여부는 어느 경로로 불렀는지가 정한다 — {@code POST /orders} 는 일반,
 * {@code POST /orders/emergency} 는 응급이다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurgeryOrderRequest {

    @NotBlank
    private String patientId;

    /**
     * 내원 식별자 — 청구 연동(SL2-72)에 필요하다.
     *
     * <p>필수로 두지 않았다. 응급은 접수보다 수술 요청이 먼저 올라오는 경우가 있어
     * 강제하면 요청 자체를 못 넣는다. 다만 이 값이 없으면 나중에 청구를 걸 수 없으므로,
     * 진료 쪽은 되도록 채워 보내야 한다.</p>
     */
    private String visitId;

    @NotBlank
    private String surgeonId;

    /** 희망 수술일 (yyyy-MM-dd). 수락 단계에서 조정될 수 있다 */
    @NotNull
    private LocalDate requestedDt;

    private String surgeryTypeCd;

    private String surgeryName;

    /**
     * 요청자(직원) 식별자.
     *
     * <p>수술 서비스에 로그인 세션이 없어 서버가 알 수 없다. 보내면 저장하고 안 보내면
     * 비워 둔다. 감사 용도로 신뢰하려면 인증 체계가 정해진 뒤 다시 봐야 한다.</p>
     */
    private String orderedBy;
}
