package kr.co.seoulit.hisback.surgery.room.service;

import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.entity.EquipmentStatus;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.repository.SurgicalEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 수술장비 관리 서비스 로직 (FR-SUR-001)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SurgicalEquipmentService {

    private final SurgicalEquipmentRepository equipmentRepository;

    /** 장비 목록 조회 (SL2-9) */
    @Transactional(readOnly = true)
    public List<SurgicalEquipmentDto> getEquipments() {
        return equipmentRepository.findByActiveTrue()
                .stream().map(SurgicalEquipmentDto::from).toList();
    }

    /** 장비 추가 (SL2-10) */
    public SurgicalEquipmentDto addEquipment(SurgicalEquipmentDto dto) {
        if (equipmentRepository.existsByEquipmentCode(dto.getEquipmentCode())) {
            throw new BusinessException("이미 존재하는 장비 코드입니다: " + dto.getEquipmentCode());
        }
        return SurgicalEquipmentDto.from(equipmentRepository.save(dto.toEntity()));
    }

    /** 장비 정보 수정 (SL2-31) */
    public SurgicalEquipmentDto updateEquipment(Long id, SurgicalEquipmentDto dto) {
        SurgicalEquipment equipment = findOrThrow(id);
        equipment.setEquipmentName(dto.getEquipmentName());
        equipment.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            equipment.setStatus(dto.getStatus());
        }
        return SurgicalEquipmentDto.from(equipment);
    }

    /** 장비 제거 (SL2-11) — 사용/출고 중이면 거부하고, 그 외에는 소프트 삭제한다. */
    public void removeEquipment(Long id) {
        SurgicalEquipment equipment = findOrThrow(id);
        if (equipment.getStatus() == EquipmentStatus.IN_USE || equipment.getStatus() == EquipmentStatus.OUT) {
            throw new BusinessException("사용/출고 중인 장비는 제거할 수 없습니다: " + equipment.getEquipmentCode());
        }
        equipment.setActive(false);
    }

    /**
     * 장비 출고/반입 처리 (SL2-12)
     *
     * @param movementType OUT(출고) 또는 IN(반입)
     * @param targetRoom   출고 시 배치할 수술방 코드 (반입 시 무시)
     */
    public SurgicalEquipmentDto move(Long id, String movementType, String targetRoom) {
        SurgicalEquipment equipment = findOrThrow(id);
        String type = movementType == null ? "" : movementType.trim().toUpperCase();
        switch (type) {
            case "OUT" -> {
                equipment.setStatus(EquipmentStatus.OUT);
                equipment.setOperatingRoom(targetRoom);
            }
            case "IN" -> {
                equipment.setStatus(EquipmentStatus.AVAILABLE);
                equipment.setOperatingRoom(null);
            }
            default -> throw new BusinessException("이동 유형은 OUT(출고) 또는 IN(반입)이어야 합니다: " + movementType);
        }
        equipment.setLastMovementType(type);
        equipment.setLastMovementDt(LocalDateTime.now());
        return SurgicalEquipmentDto.from(equipment);
    }

    private SurgicalEquipment findOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("장비 정보를 찾을 수 없습니다. id=" + id));
    }
}
