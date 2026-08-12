package kr.co.seoulit.hisback.surgery.monitoring.service;

import java.time.LocalDate;
import kr.co.seoulit.hisback.surgery.monitoring.dto.SurgeryStatusDto;

/**
 * 수술 현황 모니터링 서비스 (SL2-40 금일수술현황대시보드)
 *
 * <p>수술 목록을 상태별로 집계해서 돌려주는 것만 한다. 수술을 만들거나 상태를 바꾸는 일은
 * 여기서 하지 않는다 — 그것은 SurgeryScheduleService 소유다(§21.2 데이터는 한 곳이 소유한다).</p>
 *
 * <p><b>SL2-39 진행상태변경이 여기 없는 이유</b> — 이미
 * {@code SurgeryScheduleService.updateProgress()} 가 하고 있다. 모니터링 화면에서 부르는
 * 기능이라고 해서 모니터링 서비스에 같은 것을 또 만들면, 상태 전이 규칙이 두 곳으로 갈라져
 * 한쪽만 고치는 사고가 난다. 화면이 두 서비스를 부르는 편이 낫다.</p>
 *
 * <p>조회 전용이라 모든 메서드가 값을 돌려주기만 하고 아무것도 저장하지 않는다.</p>
 */
public interface SurgeryMonitoringService {

    /**
     * 오늘 날짜의 수술 현황을 집계한다.
     *
     * <p>대시보드가 가장 자주 부르는 메서드다. 날짜를 서버가 정하는 이유 —
     * 클라이언트 시계나 시간대가 어긋나면 "오늘"이 서로 달라진다.</p>
     *
     * <p>수술이 한 건도 없어도 예외를 던지지 않고 전부 0 인 결과를 돌려준다.
     * 없는 것은 오류가 아니라 정상적인 답이다.</p>
     */
    SurgeryStatusDto getTodayStatus();

    /**
     * 지정한 날짜의 수술 현황을 집계한다.
     *
     * <p>지난 날짜를 되짚어 보거나 다음 날 준비 상황을 미리 보는 데 쓴다.</p>
     *
     * @param surgeryDt 집계 기준일. null 이면 오늘로 본다.
     */
    SurgeryStatusDto getStatusByDate(LocalDate surgeryDt);
}
