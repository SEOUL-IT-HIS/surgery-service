package kr.co.seoulit.hisback.surgery.room.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.repository.SurgicalEquipmentRepository;
import org.springframework.stereotype.Service;

/**
 * 수술장비 관리 서비스 구현체 (OperatingRoomServiceImpl과 동일한 패턴)
 * <p>실제 로직은 이 class에 두고 SurgicalEquipmentService는 interface로 둔다.</p>
 */
@Service
public class SurgicalEquipmentServiceImpl implements SurgicalEquipmentService {

    private final SurgicalEquipmentRepository surgicalEquipmentRepository;

    public SurgicalEquipmentServiceImpl(SurgicalEquipmentRepository surgicalEquipmentRepository) {
        this.surgicalEquipmentRepository = surgicalEquipmentRepository;
    }

    @Override
    public List<SurgicalEquipmentDto> getSurgicalEquipments() {
        return surgicalEquipmentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SurgicalEquipmentDto createSurgicalEquipment(SurgicalEquipmentDto request) {
        SurgicalEquipment equipment = SurgicalEquipment.builder()
                .equipmentId(request.getEquipmentId())
                .equipmentName(request.getEquipmentName())
                .statusCd(request.getStatusCd())
                .inoutCd(request.getInoutCd())
                .build();
        return toDto(surgicalEquipmentRepository.save(equipment));
    }

    @Override
    public SurgicalEquipmentDto updateSurgicalEquipment(String surgicalEquipmentId, SurgicalEquipmentDto request) {
        SurgicalEquipment equipment = findEquipmentOrThrow(surgicalEquipmentId);
        equipment.setEquipmentName(request.getEquipmentName());
        return toDto(surgicalEquipmentRepository.save(equipment));
    }

    @Override
    public SurgicalEquipmentDto deleteSurgicalEquipment(String surgicalEquipmentId) {
        SurgicalEquipment equipment = findEquipmentOrThrow(surgicalEquipmentId);
        surgicalEquipmentRepository.delete(equipment);
        return toDto(equipment);
    }

    /** SL2-12 출고반입: inout_cd 상태 전이 (OperatingRoom changeOperatingRoomStatus와 동형) */
    @Override
    public SurgicalEquipmentDto changeInoutStatus(String equipmentId, String inoutCd) {
        SurgicalEquipment equipment = findEquipmentOrThrow(equipmentId);
        equipment.setInoutCd(inoutCd);
        return toDto(surgicalEquipmentRepository.save(equipment));
    }

    private SurgicalEquipment findEquipmentOrThrow(String equipmentId) {
        return surgicalEquipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NoSuchElementException("수술장비를 찾을 수 없습니다: " + equipmentId));
    }

    private SurgicalEquipmentDto toDto(SurgicalEquipment equipment) {
        return new SurgicalEquipmentDto(
                equipment.getEquipmentId(),
                equipment.getEquipmentName(),
                equipment.getStatusCd(),
                equipment.getInoutCd(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt());
    }
}
