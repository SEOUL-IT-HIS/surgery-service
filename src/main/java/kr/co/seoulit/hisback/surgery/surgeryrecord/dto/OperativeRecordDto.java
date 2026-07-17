package kr.co.seoulit.hisback.surgery.surgeryrecord.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술기록 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperativeRecordDto {
    private String recordId;
    private String surgeryId;
    private String procedureCd;
    private String procedureName;
    private String opStatusCd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
