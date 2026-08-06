package kr.co.seoulit.hisback.surgery.consent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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

    /** RELATION_CD(admin-service 보호자관계코드, groupId 35)를 재사용한다 — 수술 전용 코드를 따로 두지 않는다(§21.4). */
    @NotBlank
    private String signerRelationCd;

    /** 서명자 성명 — 이 화면에서 직접 입력받는 원본이라 저장한다(§14.1 스냅샷 금지의 예외) */
    @NotBlank
    private String signedBy;

    /** 서명일 — §14.2 `_dt` = DATE (yyyy-MM-dd) */
    @NotNull
    private LocalDate signedDt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
