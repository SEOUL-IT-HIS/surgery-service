package kr.co.seoulit.hisback.surgery.proceduremaster.dto;

import java.time.LocalDateTime;

/**
 * 수술항목 마스터 DTO
 */
public class SurgeryProcedureDto {
    private String procedureCd;
    private String procedureName;
    private String activeYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
