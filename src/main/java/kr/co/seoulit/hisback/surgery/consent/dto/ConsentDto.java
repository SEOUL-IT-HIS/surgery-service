package kr.co.seoulit.hisback.surgery.consent.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 동의서 응답 DTO (가이드 §11.3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDto {
    private String consentId;
    private String surgeryId;
    private String authorStaffId;
    private String consentTypeCd;
    private String signerRelationCd;
    private String signedBy;
    private LocalDate signedDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
