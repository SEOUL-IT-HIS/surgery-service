package kr.co.seoulit.hisback.surgery.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 개별 배정 요청 (SL2-13 집도의 / SL2-15 수술실 / SL2-43 마취의 / SL2-63 간호사)
 *
 * <p>네 엔드포인트가 한 필드씩만 바꾸므로 요청 모양이 같다. 필드를 각각 두고 해당 API 가
 * 자기 것만 읽는다 — API 마다 DTO 를 따로 만들면 똑같은 클래스가 넷이 된다.</p>
 *
 * <p><b>Map 을 대체하는 이유</b> — 지금까지 {@code Map<String, String>} 으로 받아
 * {@code request.get("roomCode")} 로 꺼냈다. 키를 잘못 적어도(roomCd, room_code) 오류 없이
 * null 이 되어 <b>배정이 조용히 해제</b>됐고, {@code @Valid} 를 걸 수 없었으며, Swagger 에
 * 무엇을 보내야 하는지 드러나지 않았다.</p>
 *
 * <p><b>필드를 필수로 두지 않은 이유</b> — null 이 "배정 해제"라는 뜻을 갖는다(SL2-166).
 * {@code @NotBlank} 를 걸면 해제할 방법이 사라진다. 해제인지 오타인지는 업무 규칙이라
 * 서비스가 판단한다(§11.5 — 형식은 DTO, 업무 규칙은 서비스).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {

    /** 수술실 코드. null 이면 배정 해제 */
    private String roomCode;

    /** 집도의 식별자. 병원관리 서비스 소유라 식별자만 받는다(§21.9) */
    private String surgeonId;

    /** 마취의 식별자. null 이면 배정 해제 */
    private String anesthesiologistId;

    /** 간호사 식별자. null 이면 배정 해제 */
    private String nurseId;

    /**
     * 변경·해제 사유 (SL2-166).
     *
     * <p>아직 저장하지 않는다. SURGERY 에 배정 사유를 담을 컬럼이 없고, 배정 이력 테이블도
     * 없기 때문이다. 계약에만 자리를 잡아두고, 이력 설계가 정해지면 그때 저장한다.</p>
     */
    private String reasonCd;
}
