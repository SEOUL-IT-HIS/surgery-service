package kr.co.seoulit.hisback.surgery.client;

/**
 * 수납(Billing) 서비스 연동 클라이언트 (SL2-72 수술 완료 시 청구생성 요청 Push)
 *
 * <p><b>수술 백엔드가 다른 서비스를 직접 호출하는 유일한 경우다.</b>
 * 환자·직원·공통코드 조회는 화면이 각 서비스 API 를 직접 부른다(§2.1). 수술 백엔드가
 * 대신 조회해 주면 BFF 가 되어 §21.1 을 위반한다. 반면 청구 생성은 <b>수술 완료 시점에
 * 사용자 화면 없이</b> 일어나야 하므로 서버가 직접 호출할 수밖에 없다.</p>
 *
 * <p>호출 지점 — {@code SurgeryScheduleServiceImpl.completeSurgery()}.
 * 상태가 완료(03)로 전이되는 순간 수납에 "이 수술 청구를 만들어달라"고 알린다.</p>
 *
 * <p>REST 로 부르는 이유 — 서비스 간 통신은 REST 또는 Event 로 하되 순환 의존을 만들지 않는다(§21.3).
 * 수술 → 수납 한 방향뿐이라 REST 로 충분하다.</p>
 *
 * <p>보내는 것은 <b>식별자와 사실뿐</b>이다(§21.9). 금액·단가는 수납이 계산한다.
 * 수술이 금액을 계산해 보내면 두 서비스에 같은 로직이 생겨 값이 어긋난다.</p>
 *
 * <h3>구현 전 확정이 필요한 사항</h3>
 * <p>아직 메서드를 선언하지 않은 이유는 아래 세 가지가 수납 팀과 정해지지 않아서다.
 * 임의로 만들면 나중에 전부 다시 고쳐야 한다.</p>
 * <ol>
 *   <li><b>API 계약</b> — 엔드포인트 경로, 요청 본문 필드, 응답 형태.
 *       수술이 보낼 수 있는 값은 surgeryId · patientId · 완료일시 ·
 *       수술기록(procedureCd 목록) · 예정 자원목록(SurgeryPlannedItem)이다.</li>
 *   <li><b>멱등성</b> — 같은 수술을 두 번 완료 처리하거나 재시도했을 때 청구가 중복 생성되면 안 된다.
 *       수납이 surgeryId 로 중복을 막아주는지, 수술이 요청 키를 따로 만들어 보내야 하는지 정해야 한다.</li>
 *   <li><b>실패 정책</b> — 수납이 응답하지 않을 때 수술 완료 처리를 되돌릴 것인지, 아니면
 *       완료는 그대로 두고 청구 연계만 '실패' 상태로 남길 것인지.
 *       후자라면 재시도 주체와 주기도 함께 정해야 한다.
 *       (연계 상태는 SurgeryEstimateLink.estimateStatusCd 로 표현할 수 있다)</li>
 * </ol>
 *
 * <p>확정되면 아래 형태가 될 예정이다.</p>
 * <pre>
 *   PushBillingResult pushSurgeryBilling(PushBillingRequest request);
 * </pre>
 *
 * <p>구현체는 RestClient(또는 RestTemplate)로 만들고, 수납 서비스 주소는 코드에 박지 않고
 * application.properties 의 설정값으로 둔다(§11.1).</p>
 */
public interface BillingServiceClient {
}
