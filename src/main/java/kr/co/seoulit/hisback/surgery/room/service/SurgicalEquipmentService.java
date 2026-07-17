package kr.co.seoulit.hisback.surgery.room.service;

import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import org.springframework.data.domain.Pageable;

/**
 * 수술장비 관리 서비스 로직
 */
public interface SurgicalEquipmentService {
    /** SL2-9/SL2-110: 목록을 페이지 단위로 조회한다. */
    PageResponse<SurgicalEquipmentDto> getSurgicalEquipments(Pageable pageable);

    SurgicalEquipmentDto createSurgicalEquipment(SurgicalEquipmentDto request);

    SurgicalEquipmentDto updateSurgicalEquipment(String surgicalEquipmentId, SurgicalEquipmentDto request);

    SurgicalEquipmentDto deleteSurgicalEquipment(String surgicalEquipmentId);

    //SL2-12 출고반입: inout_cd 상태 전이 (OperatingRoom changeOperatingRoomStatus와 동형)
    SurgicalEquipmentDto changeInoutStatus(String equipmentId, String inoutCd);

    SurgicalEquipmentDto getSurgicalEquipmentfindById(String equipmentId);
}
