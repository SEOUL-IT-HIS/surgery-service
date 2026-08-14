package kr.co.seoulit.hisback.surgery.surgeryorder.type;

/**
 * 수술 오더 상태 (SURGERY_ORDER.order_status_cd)
 *
 * <p>진료·응급실이 "이 환자 수술해 달라"고 보낸 요청의 처리 상태다. 수술 자체의 상태
 * ({@code SurgeryStatus})와는 다른 축이다 — 오더는 <b>받아들일지 말지</b>의 문제이고,
 * 수술은 <b>어떻게 진행됐는지</b>의 문제다.</p>
 *
 * <pre>
 *   00 접수 ──▶ 01 수락  (수술실 배정이 끝나면 따라 바뀐다)
 *          └─▶ 02 반려  (사유를 남긴다. SURGERY 는 만들지 않는다)
 * </pre>
 *
 * <p><b>'수락'은 별도 행위가 아니다.</b> 담당자가 하는 일은 수술실을 정하는 <b>배정</b>이고,
 * 수술실이 정해지는 순간 그 요청은 받아들여진 것이므로 상태가 수락으로 따라간다.
 * "받아들이되 방은 나중에"라는 중간 상태를 두지 않는 이유 — 그것은 접수(00)와
 * 구분되지 않는다. (2026-08-13 결정)</p>
 *
 * <p><b>수락 전에 SURGERY 를 만들지 않는 이유</b>(2026-08-13 결정) — 반려된 요청까지
 * 수술 행으로 남으면 "수술 건수"에 한 번도 수술이 아니었던 것이 섞인다. 요청과 수술은
 * 다른 사실이므로 다른 테이블에 둔다(§21.7 — 부모 없이 자식이 존재 가능한가, 이력이
 * 발생하는가, 취소·재접수가 가능한가).</p>
 *
 * <p>공통코드로 등록하지 않는 이유는 SurgeryStatus 와 같다 — 서버가 전이 로직에서
 * 판단하는 값이고 사용자가 화면에서 고르는 값이 아니다.</p>
 */
public final class OrderStatus {

    private OrderStatus() {
    }

    /** 접수 — 진료·응급실이 보냈고 아직 수술실 담당자가 보지 않은 상태 */
    public static final String RECEIVED = "00";

    /** 수락 — 수술실을 배정해 SURGERY 를 만들었다. order.surgery_id 가 채워진다 */
    public static final String ACCEPTED = "01";

    /** 반려 — 사유를 남기고 되돌려보냈다. 진료가 다시 요청하면 새 오더가 생긴다 */
    public static final String REJECTED = "02";
}
