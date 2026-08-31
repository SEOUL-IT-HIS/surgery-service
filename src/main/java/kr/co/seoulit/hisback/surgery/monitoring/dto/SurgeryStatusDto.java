package kr.co.seoulit.hisback.surgery.monitoring.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 현황 요약 DTO (SL2-40 금일수술현황대시보드)
 *
 * <p>어느 하루의 수술이 상태별로 몇 건인지만 담는다. 수술 한 건의 정보는
 * SurgeryDto 가 담당하므로 여기에 목록을 끼워 넣지 않는다 — 목록이 필요한 화면은
 * {@code GET /api/surgery/schedule/today} 를 따로 부른다.</p>
 *
 * <p><b>조회 전용이다.</b> 집계값이라 요청 본문으로 받을 일이 없다.</p>
 *
 * <h3>왜 상태마다 필드를 따로 두는가</h3>
 * <p>{@code Map<String, Long>} 으로 하면 상태가 늘어도 서버는 안 고쳐도 되지만,
 * 프론트가 키를 문자열로 찾게 되어 오타를 컴파일러가 못 잡는다. 수술 상태는
 * admin 공통코드가 아니라 서버 상수(SurgeryStatus)로 고정돼 있어 늘어날 일이
 * 드물기 때문에, 타입이 잡히는 쪽을 택했다.</p>
 *
 * <h3>합계를 서버가 계산해 내려주는 이유</h3>
 * <p>지금은 화면이 목록을 받아 직접 세고 있다. 그래도 이 DTO 를 두는 것은,
 * 세는 규칙(취소 건을 합계에 넣는지 등)이 화면마다 달라지면 같은 날짜인데
 * 화면마다 숫자가 다르게 보이기 때문이다. 규칙은 한 곳에만 있어야 한다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryStatusDto {

    /** 집계 기준일 */
    private LocalDate surgeryDt;

    /**
     * 그 날의 전체 건수 — 취소 건을 <b>포함</b>한다.
     *
     * <p>취소를 빼면 "오늘 3건이었는데 왜 2건으로 보이나" 하는 질문이 생긴다.
     * 취소도 그 날 있었던 일이므로 세되, cancelledCount 로 따로 구분해 보여준다.</p>
     *
     * <p>아직 수락되지 않은 오더는 여기 들어오지 않는다 — 수술이 아직 없기 때문이다.</p>
     */
    private long totalCount;

    /**
     * 접수 대기 오더 수 — 아직 배정되지 않아 조치가 필요한 요청.
     *
     * <p><b>이것만 수술이 아니라 오더를 센다</b>. 요청 단계가 SURGERY_ORDER 로
     * 옮겨져 요청접수(00) 상태의 수술이 더는 생기지 않기 때문이다. 나머지 항목은 모두
     * 그 날의 수술을 센다.</p>
     *
     * <p>그래서 <b>이 값은 아래 상태별 건수와 합산되지 않는다</b>. 상태 4개(예약·진행중·
     * 완료·취소)를 더하면 totalCount 가 되지만, 여기에 requestedCount 를 더하면 안 된다.</p>
     */
    private long requestedCount;

    /** 예약 (SurgeryStatus.SCHEDULED = "01") — 배정 완료, 시작 대기 */
    private long scheduledCount;

    /** 진행중 (SurgeryStatus.IN_PROGRESS = "02") — 지금 수술실이 물려 있는 건 */
    private long inProgressCount;

    /** 완료 (SurgeryStatus.COMPLETED = "03") */
    private long completedCount;

    /** 취소 (SurgeryStatus.CANCELLED = "04") */
    private long cancelledCount;

    /**
     * 응급 건수 (emergency_yn = 'Y').
     *
     * <p>상태별 건수와 <b>겹친다</b> — 응급이면서 진행중인 수술은 양쪽에 모두 센다.
     * 상태 5개를 더하면 totalCount 가 되지만 여기에 emergencyCount 를 더하면 안 된다.</p>
     */
    private long emergencyCount;

    /**
     * 수술실이 아직 안 잡힌 건수 (room_code 가 비어 있음).
     *
     * <p>완료·취소는 제외한다 — 이미 끝난 건에 수술실이 없는 것은 조치할 일이 아니다.
     * 이 숫자가 0 이 아니면 배정 담당자가 할 일이 남았다는 뜻이다.</p>
     */
    private long unassignedRoomCount;
}
