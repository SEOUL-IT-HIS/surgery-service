package kr.co.seoulit.hisback.surgery.schedule.type;

import java.util.Map;
import java.util.Set;

/**
 * 수술 상태 전이 규칙 (SL2-281)
 *
 * <p>어떤 상태에서 어떤 상태로 갈 수 있는지를 한 곳에 모은다. 규칙이 메서드마다
 * {@code if} 로 흩어져 있으면 새 상태가 생겼을 때 고칠 곳을 놓치고, 어떤 메서드는
 * 검사조차 없이 통과한다 — 실제로 {@code completeSurgery} 와 {@code updateProgress} 가
 * 그런 상태였다(취소된 수술을 완료 처리하는 요청이 200 으로 통과했다).</p>
 *
 * <h3>허용 전이</h3>
 * <pre>
 *   00 요청접수 ──▶ 01 예약        (배정)
 *              └─▶ 04 취소        (반려)
 *   01 예약     ──▶ 02 진행중      (시작)
 *              └─▶ 04 취소
 *   02 진행중   ──▶ 03 완료
 *   03 완료     ──▶ (없음)
 *   04 취소     ──▶ (없음)
 * </pre>
 *
 * <p><b>완료·취소는 종착점이다.</b> §21.6 이 삭제 대신 상태 변경을 권하지만, 그렇다고
 * 끝난 수술을 되돌리는 문을 열어두면 이력이 뒤엉킨다. 잘못 완료 처리한 경우는
 * 되돌리기가 아니라 별도 정정 업무로 다뤄야 하며, 그 요구사항은 아직 없다.</p>
 *
 * <p><b>여기서 예외를 던지지 않는 이유</b> — 이 클래스는 규칙만 안다. 어떤 오류코드로
 * 어떤 메시지를 줄지는 서비스가 정한다. 규칙과 예외 처리를 섞으면 테스트가 어려워진다.</p>
 */
public final class SurgeryStatusTransition {

    private SurgeryStatusTransition() {
    }

    /**
     * 현재 상태 → 갈 수 있는 상태들.
     *
     * <p>{@code Map.of} / {@code Set.of} 는 불변이라 실수로 고칠 수 없다.
     * 종착 상태(완료·취소)는 빈 집합으로 <b>명시</b>한다 — 아예 빼면 "빠뜨린 것"과
     * "갈 곳이 없는 것"이 구분되지 않는다.</p>
     */
    private static final Map<String, Set<String>> ALLOWED =
            Map.of(
                    SurgeryStatus.REQUESTED,
                            Set.of(SurgeryStatus.SCHEDULED, SurgeryStatus.CANCELLED),
                    SurgeryStatus.SCHEDULED,
                            Set.of(SurgeryStatus.IN_PROGRESS, SurgeryStatus.CANCELLED),
                    SurgeryStatus.IN_PROGRESS, Set.of(SurgeryStatus.COMPLETED),
                    SurgeryStatus.COMPLETED, Set.of(),
                    SurgeryStatus.CANCELLED, Set.of());

    /**
     * {@code from} 에서 {@code to} 로 갈 수 있는가.
     *
     * <p>{@code from} 이 표에 없는 값이면 <b>막지 않는다</b>. admin 에 상태코드가 추가됐는데
     * 여기 반영이 안 된 경우, 모르는 값을 전부 거부하면 정상 업무가 멈춘다.
     * 모르는 것은 통과시키고 아는 것만 막는다 — 수술실·장비 코드 검증에서 쓴
     * {@code hasGroup} 가드와 같은 판단이다.</p>
     *
     * <p>{@code from == null} 은 최초 등록이므로 통과시킨다.</p>
     */
    public static boolean isAllowed(String from, String to) {
        if (from == null) {
            return true;
        }
        Set<String> next = ALLOWED.get(from);
        if (next == null) {
            return true;
        }
        return next.contains(to);
    }

    /** 더 이상 상태가 바뀌지 않는 종착 상태인가 (완료·취소). */
    public static boolean isFinal(String status) {
        Set<String> next = ALLOWED.get(status);
        return next != null && next.isEmpty();
    }
}
