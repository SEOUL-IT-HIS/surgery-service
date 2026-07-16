package kr.co.seoulit.hisback.surgery.room.service;

import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.repository.SurgicalEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurgicalEquipmentServiceImpl implements SurgicalEquipmentService {

    private final SurgicalEquipmentRepository surgicalEquipmentRepository;

    public SurgicalEquipmentServiceImpl(SurgicalEquipmentRepository surgicalEquipmentRepository) {
        this.surgicalEquipmentRepository = surgicalEquipmentRepository;
    }

    @Override
    public List<SurgicalEquipmentDto> getSurgicalEquipments() {
        return surgicalEquipmentRepository.findAll().stream()
                .map(SurgicalEquipment::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public SurgicalEquipmentDto createSurgicalEquipment(SurgicalEquipmentDto request) {
        SurgicalEquipment equipment = SurgicalEquipment.builder()
                .equipmentId(request.getEquipmentId())
                .equipmentName(request.getEquipmentName())
                .build();
        return surgicalEquipmentRepository.save(equipment).toDto();
    }
}
