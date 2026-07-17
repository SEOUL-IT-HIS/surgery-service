package kr.co.seoulit.hisback.surgery.checklist.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술안전체크리스트 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryChecklistDto {
    private String checklistId;
    private String surgeryId;
    private String phaseCd;
    private String completedYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
