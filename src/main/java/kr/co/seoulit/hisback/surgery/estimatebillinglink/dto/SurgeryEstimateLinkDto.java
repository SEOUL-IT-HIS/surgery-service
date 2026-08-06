package kr.co.seoulit.hisback.surgery.estimatebillinglink.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 견적 연계 상태 DTO (SL2-67 조회 / SL2-68 상태변경)
 *
 * <p>필드명은 엔티티(SurgeryEstimateLink)와 1:1로 맞춘다.</p>
 *
 * <p>견적 금액이 없는 이유 — 가격은 수납(Billing)이 소유하며 수술은 상태만 안다(§21.2).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryEstimateLinkDto {

    // 경로 변수로 받아 컨트롤러가 덮어쓸 예정이라 제약을 걸지 않는다.
    private String surgeryId;

    @NotBlank
    private String estimateStatusCd;

    // 아직 요청 전이면 null 이라 필수로 두지 않는다.
    private LocalDateTime estimateDt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
