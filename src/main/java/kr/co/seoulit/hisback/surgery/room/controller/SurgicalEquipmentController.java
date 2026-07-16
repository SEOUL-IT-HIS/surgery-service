package kr.co.seoulit.hisback.surgery.room.controller;

import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.service.SurgicalEquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{equipment}")
    public ResponseEntity<SurgicalEquipmentDto> getSurgicalEquipment(@PathVariable String equipment) {
        return ResponseEntity.ok(surgicalEquipmentService.getSurgicalEquipment(equipment));
    }

    @PostMapping("/{equipment}")
    public ResponseEntity<SurgicalEquipmentDto> addSurgicalEquipment(@RequestBody SurgicalEquipmentDto surgicalEquipmentDto) {
        return ResponseEntity.ok(surgicalEquipmentService.addSurgicalEquipment(surgicalEquipmentDto));
    }

    @PutMapping("/{equipment}")
    public ResponseEntity<SurgicalEquipmentDto> updateSurgicalEquipment(@PathVariable String equipment, @RequestBody SurgicalEquipmentDto surgicalEquipmentDto) {
        return ResponseEntity.ok(surgicalEquipmentService.updateSurgicalEquipment(equipment, surgicalEquipmentDto));
    }

    @DeleteMapping("/{equipment}")
    public ResponseEntity<Void> deleteSurgicalEquipment(@PathVariable String equipment) {
        surgicalEquipmentService.deleteSurgicalEquipment(equipment);
        return ResponseEntity.noContent().build();
    }
}