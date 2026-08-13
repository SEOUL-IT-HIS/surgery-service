package kr.co.seoulit.hisback.surgery.schedule.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 상태변경 이력 DTO (SL2-282)
 *
 * <p>엔티티(SurgeryStatusHistory)와 필드명을 1:1로 맞춘다. 이름이 다르면 변환할 때 손으로
 * 맞춰야 하고 JSON 키도 어긋나 프론트에서 undefined 가 된다.</p>
 *
 * <p><b>조회 전용이다.</b> 이력은 상태가 바뀔 때 서버가 알아서 쌓는 것이라, 이 DTO 를
 * 요청 본문으로 받는 API 는 만들지 않는다. 이력을 클라이언트가 직접 쓸 수 있으면
 * 이력으로서의 신뢰를 잃는다.</p>
 *
 * <p>변경자 이름(changedByName)을 넣지 않은 이유 — 직원 정보는 병원관리 서비스 소유라
 * 식별자만 넘기고, 이름은 화면이 그쪽에서 조회한다(§14.1 스냅샷 금지 / §21.9).
 * 목록에서 매 행마다 부르면 N+1 이 되므로, 필요해지면 식별자 배열을 한 번에 넘기는
 * 배치 조회를 요청한다(§11.1).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryStatusHistoryDto {

    private String historyId;
    private String surgeryId;

    /** STATUS(수술 상태) 또는 PROGRESS(진행단계) — StatusChangeType */
    private String statusType;

    /** 최초 등록이면 null 이다 */
    private String beforeCd;

    private String afterCd;

    /** 취소 등 사유가 있는 전이에만 채워진다 */
    private String reasonCd;

    /** 직원 식별자. 프론트가 보내지 않으면 null 이다 */
    private String changedBy;

    /** 서버가 찍은 변경 시각 (§14.2 `_at` → TIMESTAMP) */
    private LocalDateTime changedAt;

    /**
     * 공통 감사 컬럼 (§14.1). 다른 DTO(SurgeryDto·ConsentDto 등)와 같은 자리에 같은 이름으로 둔다.
     *
     * <p>화면이 시각을 보여줄 때 쓸 것은 {@code changedAt} 이다. 이 둘은 감사용이라 목록에
     * 노출하지 않는다 — 지금은 changedAt 과 값이 같아 화면에 내면 같은 시각이 세 번 보인다.</p>
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
