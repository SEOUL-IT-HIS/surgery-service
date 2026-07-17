package kr.co.seoulit.hisback.surgery.nursingrecord.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술간호기록 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NursingRecordDto {
    private String nursingRecordId;
    private String surgeryId;
    private String itemCountResultCd;
    private String specimenBarcode;
    private String specimenTypeCd;
    private String recordStatusCd;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
