package kr.co.seoulit.hisback.surgery.surgeryplanneditem.service;

import kr.co.seoulit.hisback.surgery.surgeryplanneditem.dto.SurgeryPlannedItemDto;

import java.util.List;

/**
 * 수술 예정 자원목록 서비스 로직 (SL2-65 등록 / SL2-66 조회)
 *
 * <p>수술에 쓸 예정인 품목과 수량을 관리한다.</p>
 *
 * <p>예정 항목은 수술 Worklist 에서 사용한다.</p>
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
