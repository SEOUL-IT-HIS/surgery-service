package kr.co.seoulit.hisback.surgery.anesthesia.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 마취기록 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnesthesiaRecordDto {
    private String anesthesiaId;
    private String surgeryId;
    private String anesthesiaTypeCd;
    private String asaGradeCd;
    private String vitalSignsLog;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
