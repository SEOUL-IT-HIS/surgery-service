package kr.co.seoulit.hisback.surgery.checklist.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import kr.co.seoulit.hisback.surgery.checklist.repository.SurgeryChecklistRepository;
import org.springframework.stereotype.Service;

/**
 * 수술안전체크리스트 서비스 구현체
 * <p>CHECKLIST_STEP_CD: 01=SignIn/02=TimeOut/03=SignOut. 이전 단계가 완료(Y)되지 않았으면
 * 다음 단계 등록을 거부한다 — SignIn -&gt; TimeOut -&gt; SignOut 순서 검증(서비스 레벨).</p>
 */
@Service
public class SurgeryChecklistServiceImpl implements SurgeryChecklistService {

    private static final String PHASE_SIGN_IN = "01";
    private static final String PHASE_TIME_OUT = "02";
    private static final String PHASE_SIGN_OUT = "03";
    private static final String YES = "Y";

    private final SurgeryChecklistRepository surgeryChecklistRepository;

    public SurgeryChecklistServiceImpl(SurgeryChecklistRepository surgeryChecklistRepository) {
        this.surgeryChecklistRepository = surgeryChecklistRepository;
    }

    @Override
    public List<SurgeryChecklistDto> getChecklist(String surgeryId) {
        return surgeryChecklistRepository.findBySurgeryId(surgeryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurgeryChecklistDto createChecklistItem(SurgeryChecklistDto request) {
        String requiredPrevPhase = previousPhase(request.getPhaseCd());
        if (requiredPrevPhase != null) {
            boolean prevCompleted =
                    surgeryChecklistRepository.findBySurgeryId(request.getSurgeryId()).stream()
                            .anyMatch(
                                    c ->
                                            requiredPrevPhase.equals(c.getPhaseCd())
                                                    && YES.equals(c.getCompletedYn()));
            if (!prevCompleted) {
                throw new IllegalArgumentException(
                        "이전 단계(" + requiredPrevPhase + ")가 완료되지 않아 등록할 수 없습니다.");
            }
        }
        String checklistId =
                request.getChecklistId() != null ? request.getChecklistId() : UUID.randomUUID().toString();
        SurgeryChecklist item =
                SurgeryChecklist.builder()
                        .checklistId(checklistId)
                        .surgeryId(request.getSurgeryId())
                        .phaseCd(request.getPhaseCd())
                        .completedYn(request.getCompletedYn() != null ? request.getCompletedYn() : "N")
                        .build();
        return toDto(surgeryChecklistRepository.save(item));
    }

    @Override
    public SurgeryChecklistDto updateChecklistItem(String checklistId, String completedYn) {
        SurgeryChecklist item =
                surgeryChecklistRepository
                        .findById(checklistId)
                        .orElseThrow(() -> new NoSuchElementException("체크리스트를 찾을 수 없습니다: " + checklistId));
        item.setCompletedYn(completedYn);
        return toDto(surgeryChecklistRepository.save(item));
    }

    private String previousPhase(String phaseCd) {
        if (PHASE_TIME_OUT.equals(phaseCd)) {
            return PHASE_SIGN_IN;
        }
        if (PHASE_SIGN_OUT.equals(phaseCd)) {
            return PHASE_TIME_OUT;
        }
        return null;
    }

    private SurgeryChecklistDto toDto(SurgeryChecklist c) {
        return new SurgeryChecklistDto(
                c.getChecklistId(), c.getSurgeryId(), c.getPhaseCd(), c.getCompletedYn(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
