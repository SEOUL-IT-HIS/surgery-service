package kr.co.seoulit.hisback.surgery.estimatebillinglink.service;

import kr.co.seoulit.hisback.surgery.estimatebillinglink.dto.SurgeryPlannedItemDto;

import java.util.List;

/**
 * 수술 예정 자원목록 서비스 로직 (SL2-65 등록 / SL2-66 조회)
 *
 * <p>수술에 쓸 예정인 품목과 수량만 다룬다. 단가·금액은 수납(Billing) 소관이라
 * 수술이 갖지 않는다(§21.2). 수술은 "무엇을 몇 개 쓸 예정인지"만 알려주고
 * 금액 산정은 수납이 한다.</p>
 *
 * <p>SL2-65·66 은 Jira 설명이 비어 있는 상태에서 구현이 먼저 나갔다. 화면에서 쓰는 곳이
 * 아직 없으므로, 요구사항이 채워지면서 계약이 바뀌어도 고치는 비용은 크지 않다.</p>
 */
public interface SurgeryPlannedItemService {

    // SL2-66: 특정 수술의 예정 자원 목록을 조회한다.
    List<SurgeryPlannedItemDto> getPlannedItems(String surgeryId);

    // SL2-65: 예정 자원을 등록한다.
    SurgeryPlannedItemDto createPlannedItem(SurgeryPlannedItemDto request);

    // 예정 자원을 제거한다.
    // §21.6 은 '삭제보다 상태 변경'을 권하지만 여기서는 행을 실제로 지운다.
    // 예정 목록은 수술 전 계획이라 지운 기록이 나중에 쓰일 일이 없다고 봤다.
    // 대응하는 Jira 하위작업이 없어 등록·조회와 달리 근거가 코드에만 남아 있다.
    void deletePlannedItem(String plannedItemId);

}
