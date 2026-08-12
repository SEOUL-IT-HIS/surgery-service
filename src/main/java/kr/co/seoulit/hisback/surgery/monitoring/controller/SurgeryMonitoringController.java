package kr.co.seoulit.hisback.surgery.monitoring.controller;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import kr.co.seoulit.hisback.surgery.monitoring.dto.OperatingRoomStatusDto;
import kr.co.seoulit.hisback.surgery.monitoring.dto.SurgeryStatusDto;
import kr.co.seoulit.hisback.surgery.monitoring.service.SurgeryMonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술 현황 모니터링 컨트롤러 (SL2-40 금일수술현황대시보드)
 *
 * <p>응답은 §11.3 ApiResponse&lt;T&gt; 로 감싼다. 조회만 있으므로 GET 두 개뿐이다.</p>
 *
 * <p><b>경로를 {@code /api/surgery/monitoring} 으로 따로 둔 이유</b> —
 * {@code /schedule} 아래에 넣으면 수술 한 건을 다루는 경로들 사이에 집계 하나가 끼어
 * 무엇을 돌려주는 주소인지 헷갈린다. 돌려주는 것의 성격이 다르면 경로도 나눈다(§21.8).</p>
 *
 * <p><b>SL2-39 진행상태변경은 여기 없다</b> — {@code PATCH /api/surgery/schedule/{id}/progress}
 * 가 이미 처리한다. 모니터링 화면에서 쓰는 기능이라고 해서 같은 것을 또 열면 상태 전이
 * 규칙이 두 곳으로 갈라진다. 화면이 두 주소를 부르면 된다.</p>
 */
@RestController
@RequestMapping("/api/surgery/monitoring")
public class SurgeryMonitoringController {

    private final SurgeryMonitoringService surgeryMonitoringService;

    public SurgeryMonitoringController(SurgeryMonitoringService surgeryMonitoringService) {
        this.surgeryMonitoringService = surgeryMonitoringService;
    }

    /**
     * 오늘 수술 현황 요약.
     *
     * <p>{@code GET /api/surgery/monitoring/status/today}</p>
     *
     * <p>날짜를 안 받는 별도 주소를 둔 이유 — 대시보드가 가장 자주 부르는 요청인데,
     * 클라이언트가 "오늘"을 계산해 넘기면 단말 시계나 시간대에 따라 날짜가 어긋난다.
     * 기준일은 서버가 정한다.</p>
     */
    @GetMapping("/status/today")
    public ResponseEntity<ApiResponse<SurgeryStatusDto>> getTodayStatus() {
        return ResponseEntity.ok(ApiResponse.success(surgeryMonitoringService.getTodayStatus()));
    }

    /**
     * 지정한 날짜의 수술 현황 요약.
     *
     * <p>{@code GET /api/surgery/monitoring/status?date=2026-08-12}</p>
     *
     * <p>{@code date} 를 필수로 두지 않았다 — 빠지면 서비스가 오늘로 본다.
     * 형식이 어긋난 값({@code date=어제} 같은)이 오면 GlobalExceptionHandler 의
     * MethodArgumentTypeMismatch 처리가 400 SUR038 로 돌려준다.</p>
     *
     * @param date ISO 형식(yyyy-MM-dd). 생략하면 오늘.
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SurgeryStatusDto>> getStatusByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryMonitoringService.getStatusByDate(date)));
    }

    /**
     * SL2-287: 수술실별 진행 상태·공실 여부.
     *
     * <p>{@code GET /api/surgery/monitoring/rooms}<br>
     * {@code GET /api/surgery/monitoring/rooms?date=2026-08-12}</p>
     *
     * <p>수술실 전체를 돌려준다 — 수술이 없는 빈 방도 포함한다. 배정 담당자가 찾는 것이
     * 빈 방이라, 수술이 있는 방만 추리면 화면이 쓸모없어진다.</p>
     *
     * <p>경로를 {@code /status/rooms} 가 아니라 {@code /rooms} 로 둔 이유 — 돌려주는 것이
     * 하루 요약(status)이 아니라 방 목록이라, status 아래에 넣으면 성격이 어긋난다.</p>
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<OperatingRoomStatusDto>>> getRoomStatus(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                ApiResponse.success(surgeryMonitoringService.getRoomStatus(date)));
    }
}
