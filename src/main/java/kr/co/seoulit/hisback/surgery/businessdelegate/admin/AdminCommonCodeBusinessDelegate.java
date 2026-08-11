package kr.co.seoulit.hisback.surgery.businessdelegate.admin;

import java.util.List;
import java.util.Map;

/**
 * admin-service 공통코드 조회 창구 (§21.3 REST 직접 호출)
 *
 * <p>인터페이스를 따로 두는 이유 — 호출 방식(RestTemplate·WebClient·Kafka)이 바뀌어도
 * 이걸 쓰는 쪽은 그대로 두기 위해서다. 지금 구현은 {@link AdminCommonCodeHttpBusinessDelegate}
 * 하나뿐이고, 테스트에서는 가짜 구현을 끼우면 admin 없이도 돌릴 수 있다.</p>
 *
 * <p><b>서비스 계층에서 이걸 직접 부르지 않는다.</b> 코드값을 확인할 때마다 admin 에 물으면
 * 요청 한 건이 남의 서비스 응답 속도에 묶이고, admin 이 죽으면 수술도 같이 멈춘다.
 * 실제 판정은 {@code common/cache/CommonCodeCache} 가 메모리에서 하고, 이 창구는
 * 그 캐시를 채울 때만 쓰인다.</p>
 *
 * <p>패키지 이름이 businessdelegate 인 것은 lab-imaging-service 와 맞춘 것이다.
 * 타 서비스 호출부를 어디에 두는지 팀에서 먼저 정해진 자리다.</p>
 */
public interface AdminCommonCodeBusinessDelegate {

    /**
     * 사용중인 전체 공통코드를 그룹별로 읽어온다.
     *
     * @return 그룹코드(SURGERY_STATUS_CD 등) → 코드값 목록("00","01",…).
     *         admin 이 응답하지 않으면 예외가 그대로 올라간다 — 삼킬지 말지는 호출자가 정한다.
     */
    Map<String, List<String>> getAllCodeValues();
}
