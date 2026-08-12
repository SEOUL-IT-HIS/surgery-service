package kr.co.seoulit.hisback.surgery.monitoring.service;

import java.time.LocalDate;
import java.util.List;
import kr.co.seoulit.hisback.surgery.monitoring.dto.SurgeryStatusDto;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import kr.co.seoulit.hisback.surgery.schedule.type.SurgeryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술 현황 모니터링 서비스 구현체 (SL2-40)
 *
 * <h3>왜 리포지토리에 count 쿼리를 만들지 않고 목록을 받아 세는가</h3>
 * <p>상태별로 세면 쿼리가 5번 나가고, 응급·미배정까지 하면 7번이 된다. 하루치 수술은
 * 많아야 수십 건이라 한 번에 가져와 메모리에서 세는 편이 DB 왕복도 적고 코드도 짧다.
 * 하루 수백 건 규모가 되면 그때 {@code @Query} 로 GROUP BY 집계 한 방으로 바꾼다.</p>
 *
 * <h3>같은 목록을 한 번만 읽는 이유</h3>
 * <p>필터를 걸 때마다 리포지토리를 다시 부르면, 그 사이에 다른 사람이 상태를 바꿨을 때
 * 합계가 안 맞는다(진행중 3건인데 총 4건이 아니라 5건이 되는 식). 한 번 읽은 목록을
 * 계속 재사용해야 스냅샷이 한 시점으로 고정된다.</p>
 */
@Service
// readOnly = true — 쓰기가 없다는 것을 선언해 두면 Hibernate 가 변경감지(dirty checking)를
//   건너뛰어 조금 빨라지고, 실수로 save 를 넣었을 때 드러난다.
@Transactional(readOnly = true)
public class SurgeryMonitoringServiceImpl implements SurgeryMonitoringService {

    private final SurgeryRepository surgeryRepository;

    // 생성자 주입 — 필드에 @Autowired 를 붙이지 않는다. 생성자로 받으면 의존성이
    //   빠졌을 때 기동 시점에 바로 드러나고, final 로 둘 수 있어 나중에 바뀌지 않는다.
    public SurgeryMonitoringServiceImpl(SurgeryRepository surgeryRepository) {
        this.surgeryRepository = surgeryRepository;
    }

    @Override
    public SurgeryStatusDto getTodayStatus() {
        return getStatusByDate(LocalDate.now());
    }

    @Override
    public SurgeryStatusDto getStatusByDate(LocalDate surgeryDt) {
        // null 을 오늘로 받아준다 — 날짜를 안 보내는 것은 "오늘"을 뜻하는 흔한 관례라
        //   예외를 던지기보다 기본값을 주는 편이 화면 쪽이 단순해진다.
        LocalDate target = (surgeryDt != null) ? surgeryDt : LocalDate.now();

        List<Surgery> surgeries = surgeryRepository.findBySurgeryDt(target);

        return SurgeryStatusDto.builder()
                .surgeryDt(target)
                .totalCount(surgeries.size())
                .requestedCount(countByStatus(surgeries, SurgeryStatus.REQUESTED))
                .scheduledCount(countByStatus(surgeries, SurgeryStatus.SCHEDULED))
                .inProgressCount(countByStatus(surgeries, SurgeryStatus.IN_PROGRESS))
                .completedCount(countByStatus(surgeries, SurgeryStatus.COMPLETED))
                .cancelledCount(countByStatus(surgeries, SurgeryStatus.CANCELLED))
                .emergencyCount(countEmergency(surgeries))
                .unassignedRoomCount(countUnassignedRoom(surgeries))
                .build();
    }

    /**
     * 상태코드가 일치하는 건수.
     *
     * <p>{@code status.equals(s.getStatusCd())} 순서로 비교하는 이유 — statusCd 가 null 이어도
     * NPE 가 나지 않는다. status 는 우리가 넘기는 상수라 절대 null 이 아니다.</p>
     */
    private long countByStatus(List<Surgery> surgeries, String status) {
        return surgeries.stream().filter(s -> status.equals(s.getStatusCd())).count();
    }

    /** 응급 건수. emergency_yn 은 CHAR(1) 이라 'Y'/'N' 문자열로 들어온다(§14.2). */
    private long countEmergency(List<Surgery> surgeries) {
        return surgeries.stream().filter(s -> "Y".equals(s.getEmergencyYn())).count();
    }

    /**
     * 수술실 미배정 건수.
     *
     * <p>완료·취소는 세지 않는다 — 이미 끝난 건에 수술실이 비어 있는 것은 조치할 일이
     * 아니라서, 세면 담당자가 처리할 수 없는 숫자만 남는다.</p>
     *
     * <p>공백 문자열도 미배정으로 본다. Oracle 은 빈 문자열을 null 로 저장하지만,
     * 공백 한 칸이 들어온 경우까지 막으려면 trim 검사가 필요하다.</p>
     */
    private long countUnassignedRoom(List<Surgery> surgeries) {
        return surgeries.stream()
                .filter(s -> !SurgeryStatus.COMPLETED.equals(s.getStatusCd()))
                .filter(s -> !SurgeryStatus.CANCELLED.equals(s.getStatusCd()))
                .filter(s -> s.getRoomCode() == null || s.getRoomCode().isBlank())
                .count();
    }
}
