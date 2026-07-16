package kr.co.seoulit.hisback.surgery.room.service;

import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;

import java.util.List;

/**
 * 수술장비 관리 서비스 로직
 */
public interface SurgicalEquipmentService {
    List<SurgicalEquipmentDto> getSurgicalEquipments();

    SurgicalEquipmentDto createSurgicalEquipment(SurgicalEquipmentDto request);

    SurgicalEquipmentDto updateSurgicalEquipment(String surgicalEquipmentId, SurgicalEquipmentDto request);

    SurgicalEquipmentDto deleteSurgicalEquipment(String surgicalEquipmentId);

    /** SL2-12 출고반입: inout_cd 상태 전이 (OperatingRoom changeOperatingRoomStatus와 동형) */
    SurgicalEquipmentDto changeInoutStatus(String equipmentId, String inoutCd);
}
