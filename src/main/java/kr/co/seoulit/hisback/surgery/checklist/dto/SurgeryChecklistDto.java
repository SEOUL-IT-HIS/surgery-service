package kr.co.seoulit.hisback.surgery.checklist.dto;

import kr.co.seoulit.hisback.surgery.checklist.entity.ChecklistPhase;
import kr.co.seoulit.hisback.surgery.checklist.entity.SurgeryChecklist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술안전체크리스트 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryChecklistDto {

    private Long checklistId;
    private Long surgeryId;
    private ChecklistPhase phase;
    private String phaseLabel;
    private String items;
    private boolean completedYn;
    private String checkedBy;
    private LocalDateTime checkedDt;

    public static SurgeryChecklistDto from(SurgeryChecklist c) {
        return SurgeryChecklistDto.builder()
                .checklistId(c.getChecklistId())
                .surgeryId(c.getSurgeryId())
                .phase(c.getPhase())
                .phaseLabel(c.getPhase() != null ? c.getPhase().getLabel() : null)
                .items(c.getItems())
                .completedYn(c.isCompletedYn())
                .checkedBy(c.getCheckedBy())
                .checkedDt(c.getCheckedDt())
                .build();
    }
}
