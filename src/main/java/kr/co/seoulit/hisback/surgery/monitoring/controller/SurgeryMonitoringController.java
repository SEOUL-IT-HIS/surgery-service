package kr.co.seoulit.hisback.surgery.monitoring.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.monitoring.dto.SurgeryStatusDto;
import kr.co.seoulit.hisback.surgery.monitoring.service.SurgeryMonitoringService;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술 현황 모니터링 컨트롤러 (SL2-39 진행상태변경 / SL2-40 금일수술현황대시보드 / API-SUR-002·006)
 */
@RestController
@RequestMapping("/api/v1/surgery")
@RequiredArgsConstructor
public class SurgeryMonitoringController {

    private final SurgeryMonitoringService surgeryMonitoringService;

    /** 수술 진행상태 변경 (SL2-39 / API-SUR-002) */
    @PatchMapping("/{surgeryId}/status")
    public ApiResponse<SurgeryStatusDto> changeStatus(@PathVariable Long surgeryId,
                                                      @RequestBody StatusChangeRequest request) {
        return ApiResponse.ok("수술 진행상태가 변경되었습니다.",
                surgeryMonitoringService.changeStatus(surgeryId, request.status()));
    }

    /** 금일 수술 현황 대시보드 (SL2-40 / API-SUR-006) */
    @GetMapping("/dashboard/today")
    public ApiResponse<List<SurgeryStatusDto>> todayDashboard() {
        return ApiResponse.ok(surgeryMonitoringService.getTodayDashboard());
    }

    /** 상태 변경 요청 바디 */
    public record StatusChangeRequest(SurgeryStatus status) {
    }
}
