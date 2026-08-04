package kr.co.seoulit.hisback.surgery.global.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술 완료 이벤트 (SL2-72 수술완료 → 수납 청구 연계)
 *
 * <p><b>왜 "청구생성요청"이 아니라 "수술완료"인가</b> — 가이드 §21.1 "다른 서비스의 업무를
 * 대신 수행하지 않는다"에 따라, Surgery는 Billing에게 "청구를 만들어라"고 명령하지 않고
 * "수술이 완료되었다"는 사실만 알린다. 그 사실을 받아 청구를 생성할지는 Billing이 자기
 * 책임으로 판단한다. 이렇게 해야 결합도가 낮아진다(§21.9).</p>
 *
 * <p><b>담는 데이터 기준</b> — §21.3 "필요한 최소한의 데이터만 전달":
 * <ul>
 *   <li>참조 식별자(patientId 등)만 담고 <b>환자명·집도의명 같은 타 서비스 소유 이름은 담지 않는다</b>
 *       (§14.1 스냅샷 금지). 수신 측이 이름이 필요하면 각 서비스 API로 조회한다.</li>
 *   <li><b>가격 정보를 담지 않는다.</b> 수가/금액은 Billing이 소유한 데이터이고
 *       Surgery는 소유하지 않는다(§21.2). DDL의 SURGERY_ESTIMATE_LINK에도 "가격 정보 미보유"로
 *       명시돼 있다. Surgery는 "무슨 수술을 했는가"까지만 알린다.</li>
 * </ul>
 *
 * <p><b>TODO</b> — 수술에 실제 투입된 자원(장비/약품/재료) 목록은 SURGERY_PLANNED_ITEM에
 * 해당하지만, estimatebillinglink 패키지가 아직 빈 스텁이라 이번 범위에서 제외했다.
 * 해당 패키지 구현 후 plannedItems(itemCode/quantity 배열)를 이 이벤트에 추가한다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryCompletedEvent {

    /** 수술 식별자 — 수신 측이 상세가 필요하면 이 ID로 Surgery API를 조회한다. */
    private String surgeryId;

    /** 환자 식별자(논리 참조). 환자명은 담지 않는다(§14.1). */
    private String patientId;

    /** 수술 종류 코드 — 청구 항목 판단용. 코드 해석은 공통코드(admin-service) 기준. */
    private String surgTypeCd;

    /** 수술명 — Surgery가 직접 입력받아 소유하는 원본 데이터라 스냅샷이 아니다. */
    private String surgeryName;

    /** 실제 수술 종료일 — 청구 시점 판단 기준. DDL상 DATE라 LocalDate(§14.2 `_dt` = 날짜). */
    private LocalDate actualEndDt;

    /** 이벤트 발행 시각 — 수신 측 중복/순서 처리에 사용. */
    private LocalDateTime occurredAt;
}
