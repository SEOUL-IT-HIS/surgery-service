package kr.co.seoulit.hisback.surgery.estimatebillinglink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 예정 자원목록 DTO (SL2-65 등록 / SL2-66 조회)
 *
 * <p>필드명은 엔티티(SurgeryPlannedItem)와 1:1로 맞춘다. 이름이 다르면 변환할 때
 * 손으로 맞춰야 하고, JSON 키도 어긋나 프론트에서 undefined 가 된다.</p>
 *
 * <p>가격 필드가 없는 이유 — 단가·금액은 수납(Billing) 소관이라 수술이 갖지 않는다(§21.2).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryPlannedItemDto {

    // 서버가 UUID 로 채번하므로 등록 요청에는 없어도 된다 — 제약을 걸지 않는다.
    private String plannedItemId;

    // 경로 변수로 받아 컨트롤러가 덮어쓸 예정이라 제약을 걸지 않는다.
    private String surgeryId;

    @NotBlank
    private String itemTypeCd;

    @NotBlank
    private String itemCode;

    // 0개나 음수를 예정 수량으로 둘 수 없다.
    @NotNull
    @Positive
    private Integer quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
