package kr.co.seoulit.hisback.surgery.consent.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 동의서 요청/응답 DTO (가이드 §11.3)
 *
 * <p>필수 항목 검증(SL2-218)은 여기서 선언하고 컨트롤러가 {@code @Valid} 로 발동시킨다.
 * 실패는 GlobalExceptionHandler 가 SUR038 로 변환한다(§11.5, §15.1).</p>
 *
 * <p><b>surgeryId 에 제약을 걸지 않는 이유</b> — 컨트롤러가 경로변수 값으로 덮어쓰므로
 * 프론트가 본문에 넣지 않는 것이 정상이다. 여기에 @NotBlank 를 달면 정상 요청이 거절된다.</p>
 *
 * <p>authorStaffId 는 직원 서비스가 소유한 데이터의 참조 식별자라 선택이다(§21.9).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDto {

    private String consentId;

    private String surgeryId;

    private String authorStaffId;

    /**
     * 동의 종류. admin-service 에 CONSENT_TYPE_CD(57)가 이미 있으나 검사·영상이
     * CONTRAST/INVASIVE 처럼 영문 코드값으로 쓰고 있어 체계가 다르다.
     * 합류할지 SURG_CONSENT_CD 를 신설할지 협의 후 확정한다(§21.4). 현재 값: 01수술/02마취/03비용견적
     */
    @NotBlank
    private String consentTypeCd;

    /**
     * 동의서 수령 여부(Y/N).
     *
     * <p>서명자·서명일을 대신한다(2026-09-03). 종이에 이미 적혀 있는 값을 화면에서
     * 다시 타이핑하게 하고 있었고, 시스템이 실제로 필요한 것은 "받았는가" 하나였다.</p>
     *
     * <p>보내지 않으면 서버가 Y 로 본다 — 이 API 를 부르는 대부분이 체크를 뜻하기
     * 때문이다. 해제할 때만 명시적으로 "N" 을 보낸다.</p>
     */
    private String signedYn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
