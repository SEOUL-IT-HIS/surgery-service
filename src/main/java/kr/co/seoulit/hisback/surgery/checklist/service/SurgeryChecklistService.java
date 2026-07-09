package kr.co.seoulit.hisback.surgery.checklist.service;

import kr.co.seoulit.hisback.surgery.checklist.dto.SurgeryChecklistDto;
import kr.co.seoulit.hisback.surgery.checklist.entity.ChecklistPhase;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import kr.co.seoulit.hisback.surgery.checklist.repository.SurgeryChecklistRepository;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.schedule.repository.SurgeryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수술안전체크리스트 서비스 로직 (FR-SUR-004)
 * <p>BR-011: Sign In → Time Out → Sign Out 3단계가 순서대로 완료되어야 다음 단계로 진행할 수 있다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SurgeryChecklistService {

    private final SurgeryChecklistRepository checklistRepository;
    private final SurgeryRepository surgeryRepository;

    /**
     * 단계별 체크리스트 작성/수정 (SL2-22 / SL2-46~48 / API-SUR-003)
     * <p>동일 수술의 동일 단계가 이미 있으면 갱신(upsert)한다.</p>
     */
    public SurgeryChecklistDto submitPhase(Long surgeryId, SurgeryChecklistDto dto) {
        verifySurgeryExists(surgeryId);
        if (dto.getPhase() == null) {
            throw new BusinessException("체크리스트 단계(phase)는 필수입니다.");
        }
        if (dto.isCompletedYn()) {
            validatePreviousPhaseCompleted(surgeryId, dto.getPhase());
        }

        SurgeryChecklist entity = checklistRepository
                .findBySurgeryIdAndPhase(surgeryId, dto.getPhase())
                .orElseGet(() -> SurgeryChecklist.builder()
                        .surgeryId(surgeryId)
                        .phase(dto.getPhase())
                        .build());

        entity.setItems(dto.getItems());
        entity.setCompletedYn(dto.isCompletedYn());
        entity.setCheckedBy(dto.getCheckedBy());
        entity.setCheckedDt(LocalDateTime.now());

        return SurgeryChecklistDto.from(checklistRepository.save(entity));
    }

    /** 체크리스트 사후 수정 (SL2-23 / SL2-49) */
    public SurgeryChecklistDto update(Long checklistId, SurgeryChecklistDto dto) {
        SurgeryChecklist entity = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new BusinessException("체크리스트를 찾을 수 없습니다. checklistId=" + checklistId));
        if (dto.isCompletedYn() && !entity.isCompletedYn()) {
            validatePreviousPhaseCompleted(entity.getSurgeryId(), entity.getPhase());
        }
        entity.setItems(dto.getItems());
        entity.setCompletedYn(dto.isCompletedYn());
        entity.setCheckedBy(dto.getCheckedBy());
        entity.setCheckedDt(LocalDateTime.now());
        return SurgeryChecklistDto.from(entity);
    }

    /** 체크리스트 조회 (SL2-35) */
    @Transactional(readOnly = true)
    public List<SurgeryChecklistDto> getChecklists(Long surgeryId) {
        return checklistRepository.findBySurgeryIdOrderByPhaseAsc(surgeryId)
                .stream().map(SurgeryChecklistDto::from).toList();
    }

    /** BR-011: 직전 단계가 완료되지 않았으면 현재 단계 완료를 거부한다. */
    private void validatePreviousPhaseCompleted(Long surgeryId, ChecklistPhase phase) {
        ChecklistPhase previous = phase.previous();
        if (previous == null) {
            return;
        }
        boolean prevCompleted = checklistRepository
                .findBySurgeryIdAndPhase(surgeryId, previous)
                .map(SurgeryChecklist::isCompletedYn)
                .orElse(false);
        if (!prevCompleted) {
            throw new BusinessException(
                    "이전 단계(" + previous.getLabel() + ")가 완료되어야 " + phase.getLabel() + " 단계를 완료할 수 있습니다. (BR-011)");
        }
    }

    private void verifySurgeryExists(Long surgeryId) {
        if (!surgeryRepository.existsById(surgeryId)) {
            throw new BusinessException("수술 정보를 찾을 수 없습니다. surgeryId=" + surgeryId);
        }
    }
}
