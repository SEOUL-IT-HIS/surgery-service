package kr.co.seoulit.hisback.surgery.room.service;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.entity.SurgicalEquipment;
import kr.co.seoulit.hisback.surgery.room.repository.SurgicalEquipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** 목록 조회는 상태와 무관하게 전체를 페이지 단위로 반환한다(Room.getOperatingRooms()와 동형).
     *  사용가능한 장비만 보고 싶으면 findByStatusCd("01")을 쓰는 메서드를 추가하면 된다 —
     *  Room 쪽 getAvailableOperatingRooms()와 동일 패턴. (SL2-110: page/size/sort) */
    @Override
    public PageResponse<SurgicalEquipmentDto> getSurgicalEquipments(Pageable pageable) {
        Page<SurgicalEquipment> result = surgicalEquipmentRepository.findAll(pageable);
        return new PageResponse<>(
                result.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    // SL2-87: 장비 ID로 단건 상세 조회
    @Override
    public SurgicalEquipmentDto getSurgicalEquipmentfindById(String equipmentId) {
        return toDto(findEquipmentOrThrow(equipmentId));
    }

    @Override
    public SurgicalEquipmentDto createSurgicalEquipment(SurgicalEquipmentDto request) {
        SurgicalEquipment equipment = SurgicalEquipment.builder()
                .equipmentId(request.getEquipmentId())
                .roomCode(request.getRoomCode())
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

    // SL2-11 수술장비 제거: 개발표준가이드 §21.6/§21.8("삭제보다 상태 변경 우선")에 따라
    // 물리 삭제(repository.delete) 대신 status_cd 상태 전이로 "제거"를 표현한다.
    // (기존에는 물리 삭제였으나 가이드 위반이라 상태 전이로 교체 — OperatingRoom SL2-8과 동형)
    @Override
    public SurgicalEquipmentDto changeEquipmentStatus(String equipmentId, String statusCd) {
        SurgicalEquipment equipment = findEquipmentOrThrow(equipmentId);
        equipment.setStatusCd(statusCd);
        return toDto(surgicalEquipmentRepository.save(equipment));
    }

    // SL2-12 출고반입: inout_cd 상태 전이 (OperatingRoom changeOperatingRoomStatus와 동형)
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
                equipment.getRoomCode(),
                equipment.getEquipmentName(),
                equipment.getStatusCd(),
                equipment.getInoutCd(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt());
    }
}
