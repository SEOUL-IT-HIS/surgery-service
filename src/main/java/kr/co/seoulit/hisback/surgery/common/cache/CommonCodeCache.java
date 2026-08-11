package kr.co.seoulit.hisback.surgery.common.cache;

import jakarta.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.co.seoulit.hisback.surgery.businessdelegate.admin.AdminCommonCodeBusinessDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 공통코드 로컬 캐시.
 *
 * <p>코드값을 확인할 때마다 admin 에 물으면 요청 한 건이 남의 서비스 응답 속도에 묶이고,
 * admin 이 죽으면 수술도 같이 멈춘다. 그래서 그룹별 코드값을 메모리에 올려두고 여기서 판정한다.
 * lab-imaging-service 가 먼저 쓴 방식이고 구조를 그대로 따랐다.</p>
 *
 * <p><b>동작</b></p>
 * <ul>
 *   <li>{@code @PostConstruct} — 기동 시 1회 전체 적재</li>
 *   <li>{@code @Scheduled} — {@code app.admin-service.common-code.refresh-interval-ms} 주기로 갱신</li>
 *   <li>갱신 실패 — 예외를 삼키고 기존 캐시를 유지한다. admin 이 잠깐 죽었다고 수술이 기동에
 *       실패하거나, 멀쩡하던 캐시가 빈 값으로 덮이면 안 된다</li>
 * </ul>
 *
 * <p><b>동시성</b> — 조회는 요청 스레드에서, 갱신은 스케줄러 스레드에서 일어난다. Map 을 부분
 * 수정하지 않고 <b>새로 다 만든 뒤 참조만 통째로 교체</b>하므로 volatile 참조 하나면 충분하다.
 * 읽는 쪽은 항상 이전 스냅샷이거나 새 스냅샷을 보고, 반쯤 갱신된 중간 상태를 보지 않는다.</p>
 *
 * <p><b>아직 서비스 계층에 연결하지 않았다.</b> admin 에 수술 코드그룹 5개가 등록되지 않은
 * 상태라, 지금 검증을 켜면 멀쩡한 값이 전부 막힌다. 기동 로그의 "공통코드 캐시를 갱신했습니다.
 * 그룹 N개" 로 실제 적재를 확인하고, 등록이 끝난 뒤 붙이는 순서다.</p>
 *
 * <p>연결할 자리는 이미 정해져 있다 — {@code updateProgress} 의 progressCd,
 * {@code changeRoomStatus}·{@code changeEquipmentStatus} 의 상태값, {@code createConsent} 의
 * consentTypeCd 다. 지금은 넷 다 임의 문자열이 그대로 저장된다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonCodeCache {

    private final AdminCommonCodeBusinessDelegate adminCommonCodeClient;

    /** 그룹코드 → 사용중인 코드값 Set. 갱신 시 참조를 통째로 교체한다. */
    private volatile Map<String, Set<String>> codesByGroup = Map.of();

    @PostConstruct
    public void loadOnStartup() {
        log.info("공통코드 캐시 최초 적재를 시작합니다.");
        refresh();
    }

    @Scheduled(
            fixedRateString = "${app.admin-service.common-code.refresh-interval-ms}",
            initialDelayString = "${app.admin-service.common-code.refresh-interval-ms}")
    public void refreshPeriodically() {
        refresh();
    }

    /**
     * 코드값이 해당 그룹에서 쓰이는 값인지 확인한다.
     *
     * <p><b>캐시에 없는 그룹은 false 다.</b> "아직 적재 안 된 그룹"과 "정말 틀린 코드값"이
     * 구분되지 않으므로, 호출부를 연결하기 전에 캐시가 실제로 채워지는지 반드시 확인해야 한다.</p>
     */
    public boolean isValid(String groupCode, String code) {
        if (groupCode == null || code == null) {
            return false;
        }
        Set<String> codes = codesByGroup.get(groupCode);
        return codes != null && codes.contains(code);
    }

    /**
     * 그 그룹을 캐시가 알고 있는지.
     *
     * <p><b>검증 전에 이걸 먼저 물어야 한다.</b> {@link #isValid} 는 모르는 그룹에도 false 를
     * 주므로, 그것만 보고 거절하면 두 상황에서 멀쩡한 요청이 막힌다.</p>
     * <ul>
     *   <li>기동 시 admin 이 꺼져 있어 캐시가 통째로 비어 있을 때 — 상태 변경이 전부 막힌다</li>
     *   <li>admin 에 그룹을 방금 추가했는데 아직 갱신 주기가 안 돌았을 때 — 최대 10분간 막힌다</li>
     * </ul>
     *
     * <p>그래서 호출부는 {@code hasGroup(g) && !isValid(g, code)} 로 쓴다.
     * "판정할 수 있는데 값이 목록에 없다"일 때만 거절한다는 뜻이다. 판정 자체가 불가능하면
     * 통과시킨다 — 코드값 하나 잘못 들어오는 것보다 수술실 상태 변경이 막히는 쪽이 위험하다.</p>
     */
    public boolean hasGroup(String groupCode) {
        return groupCode != null && codesByGroup.containsKey(groupCode);
    }

    /** 적재된 그룹 수 — 기동 확인·모니터링용. */
    public int getCachedGroupCount() {
        return codesByGroup.size();
    }

    /** admin 에서 전체 공통코드를 다시 읽어 캐시를 교체한다. 실패하거나 비면 기존 캐시를 유지한다. */
    private void refresh() {
        try {
            Map<String, List<String>> loaded = adminCommonCodeClient.getAllCodeValues();

            if (loaded == null || loaded.isEmpty()) {
                // 멀쩡한 기존 캐시를 빈 값으로 덮어쓰지 않는다
                log.warn(
                        "공통코드 조회 결과가 비어 있어 캐시를 갱신하지 않습니다. (기존 {}개 그룹 유지)",
                        codesByGroup.size());
                return;
            }

            Map<String, Set<String>> refreshed = new LinkedHashMap<>();
            loaded.forEach((groupCode, codes) -> refreshed.put(groupCode, Set.copyOf(codes)));
            this.codesByGroup = Map.copyOf(refreshed);

            log.info("공통코드 캐시를 갱신했습니다. 그룹 {}개", refreshed.size());

        } catch (Exception e) {
            // 예외가 새어나가면 @PostConstruct 는 기동 실패, @Scheduled 는 이후 실행 중단으로 이어진다.
            // 갱신에 실패해도 서비스는 기존 캐시로 계속 돌아야 하므로 삼키고 로그만 남긴다.
            log.error(
                    "공통코드 캐시 갱신에 실패했습니다. 기존 캐시({}개 그룹)를 유지합니다.",
                    codesByGroup.size(),
                    e);
        }
    }
}
