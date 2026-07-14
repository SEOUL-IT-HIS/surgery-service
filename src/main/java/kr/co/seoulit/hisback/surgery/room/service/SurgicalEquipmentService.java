package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.repository.SurgicalEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수술장비 관리 서비스 로직
 * <p>현재 SL2-9(조회)/SL2-10(추가)만 구현. 제거(SL2-11)·출고반입관리(SL2-12)·
 * 정보수정(SL2-31)은 아직 "해야 할 일" 상태라 이 서비스에서 다루지 않는다.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurgicalEquipmentService {

    private final SurgicalEquipmentRepository surgicalEquipmentRepository;

    /** SL2-9: 수술장비 목록 조회 */
    public List<SurgicalEquipmentDto.Response> getEquipments() {
        return surgicalEquipmentRepository.findAll().stream()
                .map(SurgicalEquipmentDto.Response::from)
                .toList();
    }

    /** SL2-10: 수술장비 추가 */
    @Transactional
    public SurgicalEquipmentDto.Response createEquipment(SurgicalEquipmentDto.CreateRequest request) {
        SurgicalEquipment equipment = SurgicalEquipment.create(request.roomCode(), request.equipmentName());
        return SurgicalEquipmentDto.Response.from(surgicalEquipmentRepository.save(equipment));
    }
}
