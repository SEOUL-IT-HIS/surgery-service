package kr.co.seoulit.hisback.surgery.estimatebillinglink.service;

/**
 * 수술 예정 자원목록 서비스 로직 (SL2-65 등록 / SL2-66 조회)
 *
 * <p><b>보류 중이다.</b> SL2-65·66 은 Jira 설명이 비어 있어 요구사항이 확정되지 않았다.
 * 스프린트 7 · 우선순위 Lowest 라 아직 착수 시점도 아니다.
 * 아래 초안은 설명이 채워지면 검토 후 되살린다.</p>
 *
 * <p>수술에 쓸 예정인 품목과 수량만 다룬다. 단가·금액은 수납(Billing) 소관이라
 * 수술이 갖지 않는다(§21.2).</p>
 */
public interface SurgeryPlannedItemService {

    /* ── 초안 (SL2-65·66 요구사항 확정 후 주석 해제) ──────────────────────
     *
     * // SL2-66: 특정 수술의 예정 자원 목록을 조회한다.
     * List<SurgeryPlannedItemDto> getPlannedItems(String surgeryId);
     *
     * // SL2-65: 예정 자원을 등록한다.
     * SurgeryPlannedItemDto createPlannedItem(SurgeryPlannedItemDto request);
     *
     * // 예정 자원을 제거한다.
     * // 판단 필요 — 여기서는 실제로 행을 지우도록 잡았다. §21.6 은 '삭제보다 상태 변경'을
     * // 권하지만, 예정 목록은 수술 전 계획이라 이력으로서의 가치가 없다고 봤다.
     * void deletePlannedItem(String plannedItemId);
     *
     * ────────────────────────────────────────────────────────────── */
}
