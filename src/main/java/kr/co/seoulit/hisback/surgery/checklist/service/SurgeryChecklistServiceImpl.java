package kr.co.seoulit.hisback.surgery.checklist.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import kr.co.seoulit.hisback.surgery.checklist.repository.SurgeryChecklistRepository;
import kr.co.seoulit.hisback.surgery.common.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.common.exception.ErrorCode;
import kr.co.seoulit.hisback.surgery.schedule.entity.Surgery;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import kr.co.seoulit.hisback.surgery.schedule.service.SurgeryGuard;
import kr.co.seoulit.hisback.surgery.schedule.type.SurgeryStatus;
import org.springframework.stereotype.Service;

/**
 * 수술안전체크리스트 서비스 구현체 (SL2-4)
 *
 * <p>CHECKLIST_STEP_CD: 01=SignIn/02=TimeOut/03=SignOut. 이전 단계가 완료(Y)되지 않았으면
 * 다음 단계 등록을 거부한다 — SignIn -&gt; TimeOut -&gt; SignOut 순서 검증(서비스 레벨).</p>
 *
 * <p>이 순서를 강제하는 이유는 업무 규칙이다. WHO 수술안전 체크리스트는 마취 전(SignIn),
 * 절개 직전(TimeOut), 수술실 퇴실 전(SignOut) 세 시점에 각각 확인하도록 돼 있다.
 * 건너뛰면 확인 자체가 무의미해지므로 화면이 아니라 서버에서 막는다.</p>
 *
 * <p>순서 검증을 컨트롤러가 아니라 여기 두는 이유 — 화면이 여러 개 생기거나 API를 직접
 * 호출해도 규칙이 동일하게 적용돼야 한다. 컨트롤러는 경로만 책임진다.</p>
 */
@Service
public class SurgeryChecklistServiceImpl implements SurgeryChecklistService {

    // 코드 카탈로그는 admin-service 소관이라 값만 상수로 들고 쓴다(§21.4).
    // 문자열을 코드 곳곳에 흩어놓으면 오타를 컴파일러가 못 잡으므로 한곳에 모은다.
    private static final String PHASE_SIGN_IN = "01";
    private static final String PHASE_TIME_OUT = "02";
    private static final String PHASE_SIGN_OUT = "03";
    private static final String YES = "Y";

    private final SurgeryChecklistRepository surgeryChecklistRepository;

    /** SL2-223: 하위 목록 조회 전에 수술 존재를 확인한다 */
    private final SurgeryGuard surgeryGuard;

    /** 수술 상태를 읽기 위해서만 쓴다 — 체크리스트를 쓸 수 있는 상태인지 판단한다 */
    private final SurgeryRepository surgeryRepository;

    public SurgeryChecklistServiceImpl(
            SurgeryChecklistRepository surgeryChecklistRepository,
            SurgeryGuard surgeryGuard,
            SurgeryRepository surgeryRepository) {
        this.surgeryChecklistRepository = surgeryChecklistRepository;
        this.surgeryGuard = surgeryGuard;
        this.surgeryRepository = surgeryRepository;
    }

    /**
     * 체크리스트를 쓸 수 있는 수술인지 확인한다.
     *
     * <p>예약(01)·진행중(02)만 허용한다. 완료(03)·취소(04)는 이미 끝난 수술이라
     * 지금 와서 안전 확인을 기록하는 것이 사실과 맞지 않는다.</p>
     */
    private void requireChecklistWritable(String surgeryId) {
        Surgery surgery =
                surgeryRepository
                        .findById(surgeryId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.SURGERY_NOT_FOUND, surgeryId));

        String status = surgery.getStatusCd();
        if (!SurgeryStatus.SCHEDULED.equals(status) && !SurgeryStatus.IN_PROGRESS.equals(status)) {
            throw new BusinessException(
                    ErrorCode.INVALID_SURGERY_STATUS, "체크리스트 작성 불가 상태=" + status);
        }
    }

    /** SL2-35: 특정 수술의 체크리스트 전체 조회 — 단계 순서와 무관하게 등록된 항목을 모두 준다. */
    @Override
    public List<SurgeryChecklistDto> getChecklist(String surgeryId) {
        // SL2-223: 없는 수술이면 빈 목록이 아니라 404 다
        surgeryGuard.requireExists(surgeryId);
        return surgeryChecklistRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * SL2-46/47/48: 체크리스트 항목 등록
     *
     * <p>등록 전에 이전 단계 완료 여부를 확인한다. SignIn(01)은 첫 단계라 검사를 건너뛰고,
     * TimeOut(02)은 SignIn 이, SignOut(03)은 TimeOut 이 완료(Y)돼 있어야 한다.</p>
     */
    @Override
    public SurgeryChecklistDto createChecklistItem(SurgeryChecklistDto request) {
        // 대상 수술이 실재해야 한다 — 조회는 이미 막고 있었는데 등록만 빠져 있었다.
        surgeryGuard.requireExists(request.getSurgeryId());

        // 끝난 수술에는 체크리스트를 남기지 않는다.
        //
        //   WHO 체크리스트는 수술이 진행되는 동안 하는 확인 절차다. 완료·취소된 수술에
        //   Sign Out 을 새로 다는 것은 사후에 기록을 만드는 일이라 의무기록으로 성립하지
        //   않는다. 검사가 없던 동안에는 취소된 수술에도 체크리스트가 붙었다.
        //
        //   예약(01)을 허용하는 이유 — Sign In 은 마취 시작 전이라 수술이 시작되기 전에
        //   수행한다. 진행중(02)만 허용하면 정상 업무가 막힌다.
        requireChecklistWritable(request.getSurgeryId());

        // SignIn(01)이면 null 이 돌아와 검사를 건너뛴다 — 첫 단계라 앞선 단계가 없다
        String requiredPrevPhase = previousPhase(request.getPhaseCd());
        if (requiredPrevPhase != null) {
            boolean prevCompleted =
                    surgeryChecklistRepository.findBySurgeryId(request.getSurgeryId()).stream()
                            .anyMatch(
                                    c ->
                                            requiredPrevPhase.equals(c.getPhaseCd())
                                                    && YES.equals(c.getCompletedYn()));
            if (!prevCompleted) {
                throw new BusinessException(
                        ErrorCode.CHECKLIST_PREV_PHASE_INCOMPLETE, "이전 단계=" + requiredPrevPhase);
            }
        }
        // PK는 내부 식별자라 서버가 UUID로 채번한다(§14.2 `_id` → VARCHAR2(36))
        String checklistId =
                request.getChecklistId() != null ? request.getChecklistId() : UUID.randomUUID().toString();
        SurgeryChecklist item =
                SurgeryChecklist.builder()
                        .checklistId(checklistId)
                        .surgeryId(request.getSurgeryId())
                        .phaseCd(request.getPhaseCd())
                        // 완료 여부를 안 보내면 미완료(N)로 시작한다 — 등록과 완료 확인은 별개 행위다
                        .completedYn(request.getCompletedYn() != null ? request.getCompletedYn() : "N")
                        .build();
        return toDto(surgeryChecklistRepository.save(item));
    }

    /**
     * SL2-49: 체크리스트 완료 여부 변경(사후 수정)
     *
     * <p>단계 순서를 여기서 다시 검사하지 않는 이유 — 순서는 '등록' 시점에 이미 강제했다.
     * 잘못 체크한 것을 되돌리는 것까지 막으면 현장에서 고칠 방법이 없어진다.</p>
     */
    @Override
    public SurgeryChecklistDto updateChecklistItem(String checklistId, String completedYn) {
        SurgeryChecklist item =
                surgeryChecklistRepository
                        .findById(checklistId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND, checklistId));

        // 등록과 같은 규칙 — 끝난 수술의 체크리스트는 되돌리지도 못한다
        requireChecklistWritable(item.getSurgeryId());

        item.setCompletedYn(completedYn);
        return toDto(surgeryChecklistRepository.save(item));
    }

    /**
     * 해당 단계 직전에 완료돼 있어야 하는 단계를 돌려준다.
     *
     * <p>SignIn(01)은 첫 단계라 {@code null} 을 돌려주고, 호출부는 이를 '검사 없음'으로 읽는다.
     * 단계가 늘어나면 이 메서드만 고치면 되도록 순서 지식을 한곳에 모아뒀다.</p>
     */
    private String previousPhase(String phaseCd) {
        if (PHASE_TIME_OUT.equals(phaseCd)) {
            return PHASE_SIGN_IN;
        }
        if (PHASE_SIGN_OUT.equals(phaseCd)) {
            return PHASE_TIME_OUT;
        }
        return null;
    }

    /** 엔티티 → DTO 변환. 엔티티를 응답에 직접 쓰지 않아 테이블 구조가 API 계약에 새지 않는다. */
    private SurgeryChecklistDto toDto(SurgeryChecklist c) {
        return new SurgeryChecklistDto(
                c.getChecklistId(),
                c.getSurgeryId(),
                c.getPhaseCd(),
                c.getCompletedYn(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
