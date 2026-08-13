package kr.co.seoulit.hisback.surgery.consent.type;

/**
 * 동의 종류 코드 (CONSENT.consent_type_cd)
 *
 * <p>수술 서비스가 다루는 세 가지다. 2026-08-10 에 수술 기준 숫자 코드로 확정했다.</p>
 *
 * <h3>마취 동의서를 별도 테이블로 두지 않는 이유</h3>
 * <p>{@code ANESTHESIA_CONSENT} 테이블을 만들면 "이 수술에 마취 동의를 받았다"는 같은 사실이
 * CONSENT 와 두 곳에 저장된다. 동의서는 종류만 다를 뿐 저장하는 내용(서명자·서명일·확인자)이
 * 같아서, 종류 코드로 구분하는 편이 맞다(§21.7 — 부모 없이 자식이 존재할 이유가 없고,
 * 같은 FK 가 여러 번 저장되는 1:N 구조다). 같은 수술에 같은 종류를 두 번 넣는 것은
 * 백엔드가 막는다(SUR044).</p>
 *
 * <h3>왜 공통코드 캐시로 검증하지 않는가</h3>
 * <p>admin 의 {@code CONSENT_TYPE_CD} 그룹에는 검사·영상이 쓰는 {@code CONTRAST}·
 * {@code INVASIVE} 같은 영문 코드가 섞여 있다. 캐시로 검증하면 그 값들도 통과해버려,
 * 수술 동의서에 조영제 동의가 들어올 수 있다. 우리가 다루는 세 개만 상수로 못박는다.</p>
 *
 * <p>영문 코드를 숫자로 통일할지는 검사·영상 담당자와 정해야 한다(§21.4). 그때까지는
 * 화면도 이 세 개만 걸러 보여준다(ConsentPanel 의 SURGERY_CONSENT_CODES).</p>
 */
public final class ConsentType {

    private ConsentType() {
    }

    /** 수술 동의서 */
    public static final String SURGERY = "01";

    /** 마취 동의서 — 마취 전 평가를 기록하려면 이것이 먼저 있어야 한다(SL2-244) */
    public static final String ANESTHESIA = "02";

    /** 비용 견적 동의서 */
    public static final String ESTIMATE = "03";
}
