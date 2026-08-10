package kr.co.seoulit.hisback.surgery.estimatebillinglink.service;

/**
 * 수술 견적 연계 상태 서비스 로직 (SL2-67 조회 / SL2-68 상태변경)
 *
 * <p><b>보류 중이다.</b> SL2-67·68 도 Jira 설명이 비어 있다.
 * 스프린트 7 · 우선순위 Lowest.</p>
 *
 * <p>수납(Billing)과의 연계가 어디까지 진행됐는지만 다룬다. 견적 금액은 수납이 소유한다(§21.2).
 * 수술 완료 시 청구 생성을 실제로 호출하는 일(SL2-72)은 BillingServiceClient 소관이며,
 * 그쪽도 API 계약이 확정되지 않았다.</p>
 */
public interface SurgeryEstimateLinkService {

    /* ── 초안 (SL2-67·68 요구사항 확정 후 주석 해제) ──────────────────────
     *
     * // SL2-67: 수술의 견적 연계 상태를 조회한다.
     * // 판단 필요 — 아직 연계를 시작하지 않은 수술이면 행 자체가 없다.
     * // 예외 대신 null 을 돌려줘 "아직 연계 안 함"으로 읽게 할지 정해야 한다.
     * SurgeryEstimateLinkDto getEstimateLink(String surgeryId);
     *
     * // SL2-68: 견적 연계 상태를 변경한다.
     * // 판단 필요 — PK 가 surgery_id 인 1:1 구조라 등록·수정을 나누지 않고
     * // upsert 로 잡았다. 이게 맞는지 확인이 필요하다.
     * SurgeryEstimateLinkDto changeEstimateStatus(String surgeryId, SurgeryEstimateLinkDto request);
     *
     * ────────────────────────────────────────────────────────────── */
}
