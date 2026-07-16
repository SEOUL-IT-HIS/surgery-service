package kr.co.seoulit.hisback.surgery.room.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.service.SurgicalEquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 수술장비 관리 컨트롤러 (SL2-9 조회 / SL2-10 추가 / SL2-11 제거 / SL2-12 출고반입관리 / SL2-31 정보수정)
 */

@RestController
@RequestMapping("/api/v1/surgery/equipment")
public class SurgicalEquipmentController {
    private final SurgicalEquipmentService surgicalEquipmentService;

    public SurgicalEquipmentController(SurgicalEquipmentService surgicalEquipmentService) {
        this.surgicalEquipmentService = surgicalEquipmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SurgicalEquipmentDto>>> getEquipments(){
        return ResponseEntity.ok(ApiResponse.success(surgicalEquipmentService.getSurgicalEquipments()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> createEquipment(@RequestBody SurgicalEquipmentDto equipmentDto) {
        SurgicalEquipmentDto created = surgicalEquipmentService.createSurgicalEquipment(equipmentDto);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> updateEquipment(@PathVariable String equipmentId, @RequestBody SurgicalEquipmentDto equipmentDto) {
        SurgicalEquipmentDto updated = surgicalEquipmentService.updateSurgicalEquipment(equipmentId, equipmentDto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteEquipment(@PathVariable String equipmentId) {
        surgicalEquipmentService.deleteSurgicalEquipment(equipmentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //SL2-12: 출고/반입은 물리 변경이 아니라 inout_cd 상태 전이로 표현한다(OperatingRoom 상태변경과 동형)
    @PatchMapping("/{equipmentId}/inout")
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> changeInout(
            @PathVariable String equipmentId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        surgicalEquipmentService.changeInoutStatus(equipmentId, request.get("inoutCd"))));
    }
}