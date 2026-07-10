package kr.co.seoulit.hisback.surgery.anesthesia.dto;

import kr.co.seoulit.hisback.surgery.anesthesia.entity.AnesthesiaRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 마취기록 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaRecordDto {

    private Long anesthesiaId;
    private Long surgeryId;
    private String anesthesiaType;
    private String preAnesthesiaEval;
    private String vitalSignsLog;
    private String drugLog;
    private String recordedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AnesthesiaRecordDto from(AnesthesiaRecord r) {
        return AnesthesiaRecordDto.builder()
                .anesthesiaId(r.getAnesthesiaId())
                .surgeryId(r.getSurgeryId())
                .anesthesiaType(r.getAnesthesiaType())
                .preAnesthesiaEval(r.getPreAnesthesiaEval())
                .vitalSignsLog(r.getVitalSignsLog())
                .drugLog(r.getDrugLog())
                .recordedBy(r.getRecordedBy())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    public AnesthesiaRecord toEntity(Long surgeryId) {
        return AnesthesiaRecord.builder()
                .surgeryId(surgeryId)
                .anesthesiaType(anesthesiaType)
                .preAnesthesiaEval(preAnesthesiaEval)
                .vitalSignsLog(vitalSignsLog)
                .drugLog(drugLog)
                .recordedBy(recordedBy)
                .build();
    }
}
