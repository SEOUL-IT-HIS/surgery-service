package kr.co.seoulit.hisback.surgery.schedule.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.schedule.dto.SurgeryDto;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 수술 스케줄링 컨트롤러
 * (SL2-36 등록 / SL2-37 수정 / SL2-33 취소 / SL2-25 조회 / SL2-13 집도의관리 / SL2-15 수술실배정관리 / SL2-44 응급등록)
 */
@RestController
@RequestMapping("/api/v1/surgery/schedule")
@RequiredArgsConstructor
public class SurgeryScheduleController {

    private final SurgeryScheduleService surgeryScheduleService;

    /** 수술 스케줄 등록 (SL2-36 / API-SUR-001) */
    @PostMapping
    public ApiResponse<SurgeryDto> register(@RequestBody SurgeryDto dto) {
        return ApiResponse.ok("수술 스케줄이 등록되었습니다.", surgeryScheduleService.register(dto));
    }

    /** 응급 수술 등록 (SL2-44) */
    @PostMapping("/emergency")
    public ApiResponse<SurgeryDto> registerEmergency(@RequestBody SurgeryDto dto) {
        return ApiResponse.ok("응급 수술이 등록되었습니다.", surgeryScheduleService.registerEmergency(dto));
    }

    /** 수술 일정 목록 조회 (SL2-25) — date(yyyy-MM-dd) 미지정 시 전체 */
    @GetMapping
    public ApiResponse<List<SurgeryDto>> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(surgeryScheduleService.getSchedules(date));
    }

    /** 수술 일정 단건 조회 */
    @GetMapping("/{surgeryId}")
    public ApiResponse<SurgeryDto> get(@PathVariable String surgeryId) {
        return ApiResponse.ok(surgeryScheduleService.getSchedule(surgeryId));
    }

    /** 수술 일정 수정 (SL2-37) */
    @PutMapping("/{surgeryId}")
    public ApiResponse<SurgeryDto> update(@PathVariable String surgeryId, @RequestBody SurgeryDto dto) {
        return ApiResponse.ok("수술 스케줄이 수정되었습니다.", surgeryScheduleService.update(surgeryId, dto));
    }

    /**
     * 수술 일정 취소 (SL2-33)
     * <p>물리 DELETE 대신 상태 전이(PATCH .../cancel)로 처리한다.
     * (개발표준가이드 §14 취지: 이력 보존을 위해 물리 삭제 지양)</p>
     */
    @PatchMapping("/{surgeryId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String surgeryId) {
        surgeryScheduleService.cancel(surgeryId);
        return ApiResponse.ok("수술 스케줄이 취소되었습니다.", null);
    }
}
