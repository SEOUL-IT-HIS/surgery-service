package kr.co.seoulit.hisback.surgery.monitoring.service;

import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.monitoring.dto.SurgeryStatusDto;
import kr.co.seoulit.hisback.surgery.nursing.repository.NursingRecordRepository;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.entity.SurgeryStatus;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 수술 현황 모니터링 서비스 로직 (FR-SUR-005)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SurgeryMonitoringService {

    private final SurgeryRepository surgeryRepository;
    private final NursingRecordRepository nursingRecordRepository;

    /**
     * 수술 진행상태 변경 (SL2-39 / API-SUR-002)
     * <p>수술중 진입 시 실제 시작시각, 완료 시 실제 종료시각을 자동 기록한다.
     * BR-013: 물품 카운트 불일치(미해결)가 있으면 '완료' 전이를 거부한다.</p>
     */
    public SurgeryStatusDto changeStatus(Long surgeryId, SurgeryStatus status) {
        if (status == null) {
            throw new BusinessException("변경할 상태(status)는 필수입니다.");
        }
        Surgery surgery = surgeryRepository.findById(surgeryId)
                .orElseThrow(() -> new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId));

        if (status == SurgeryStatus.COMPLETED
                && nursingRecordRepository.existsBySurgeryIdAndCountFinalIsNotNullAndCountMatchedFalse(surgeryId)) {
            throw new BusinessException("수술 물품 카운트가 일치하지 않습니다. 재확인(X-ray 등) 완료 전까지 수술을 종료할 수 없습니다. (BR-013)");
        }

        surgery.setStatus(status);
        LocalDateTime now = LocalDateTime.now();
        if (status == SurgeryStatus.IN_PROGRESS && surgery.getActualStartDt() == null) {
            surgery.setActualStartDt(now);
        }
        if (status == SurgeryStatus.COMPLETED && surgery.getActualEndDt() == null) {
            surgery.setActualEndDt(now);
        }
        return SurgeryStatusDto.from(surgery);
    }

    /**
     * 금일 수술 현황 대시보드 (SL2-40 / API-SUR-006)
     * <p>수술실별로 예정시각 순 정렬 후, 직전 수술 종료~다음 수술 시작 간 턴오버 타임(SL2-50)을 계산한다.</p>
     */
    @Transactional(readOnly = true)
    public List<SurgeryStatusDto> getTodayDashboard() {
        LocalDate today = LocalDate.now();
        List<Surgery> surgeries = surgeryRepository.findByScheduledDtBetweenOrderByScheduledDtAsc(
                today.atStartOfDay(), today.atTime(LocalTime.MAX));

        // 수술실별로 그룹핑하여 시각 순으로 턴오버 계산
        Map<String, List<Surgery>> byRoom = new HashMap<>();
        for (Surgery s : surgeries) {
            byRoom.computeIfAbsent(s.getOperatingRoom() == null ? "-" : s.getOperatingRoom(),
                    k -> new ArrayList<>()).add(s);
        }

        List<SurgeryStatusDto> result = new ArrayList<>();
        for (List<Surgery> roomSurgeries : byRoom.values()) {
            roomSurgeries.sort(Comparator.comparing(Surgery::getScheduledDt,
                    Comparator.nullsLast(Comparator.naturalOrder())));
            Surgery previous = null;
            for (Surgery s : roomSurgeries) {
                SurgeryStatusDto dto = SurgeryStatusDto.from(s);
                dto.setTurnoverMinutes(calcTurnoverMinutes(previous, s));
                result.add(dto);
                previous = s;
            }
        }
        result.sort(Comparator.comparing(SurgeryStatusDto::getScheduledDt,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }

    /** 직전 수술의 실제 종료시각과 본 수술의 실제 시작시각 사이 분(minute). 계산 불가 시 null */
    private Long calcTurnoverMinutes(Surgery previous, Surgery current) {
        if (previous == null || previous.getActualEndDt() == null || current.getActualStartDt() == null) {
            return null;
        }
        long minutes = Duration.between(previous.getActualEndDt(), current.getActualStartDt()).toMinutes();
        return Math.max(0, minutes);
    }
}
