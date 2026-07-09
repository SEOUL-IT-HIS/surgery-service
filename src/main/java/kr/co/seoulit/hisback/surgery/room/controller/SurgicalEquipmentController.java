package kr.co.seoulit.hisback.surgery.room.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.service.SurgicalEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 수술장비 관리 컨트롤러 (SL2-9 조회 / SL2-10 추가 / SL2-11 제거 / SL2-12 출고반입관리 / SL2-31 정보수정)
 */
@RestController
@RequestMapping("/api/v1/surgery/equipments")
@RequiredArgsConstructor
public class SurgicalEquipmentController {

    private final SurgicalEquipmentService surgicalEquipmentService;

    /** 장비 목록 조회 (SL2-9) */
    @GetMapping
    public ApiResponse<List<SurgicalEquipmentDto>> list() {
        return ApiResponse.ok(surgicalEquipmentService.getEquipments());
    }

    /** 장비 추가 (SL2-10) */
    @PostMapping
    public ApiResponse<SurgicalEquipmentDto> add(@RequestBody SurgicalEquipmentDto dto) {
        return ApiResponse.ok("장비가 추가되었습니다.", surgicalEquipmentService.addEquipment(dto));
    }

    /** 장비 정보 수정 (SL2-31) */
    @PutMapping("/{id}")
    public ApiResponse<SurgicalEquipmentDto> update(@PathVariable Long id, @RequestBody SurgicalEquipmentDto dto) {
        return ApiResponse.ok("장비 정보가 수정되었습니다.", surgicalEquipmentService.updateEquipment(id, dto));
    }

    /** 장비 출고/반입 처리 (SL2-12) */
    @PatchMapping("/{id}/movement")
    public ApiResponse<SurgicalEquipmentDto> move(@PathVariable Long id,
                                                  @RequestParam String type,
                                                  @RequestParam(required = false) String targetRoom) {
        return ApiResponse.ok("장비 출고/반입이 처리되었습니다.",
                surgicalEquipmentService.move(id, type, targetRoom));
    }

    /** 장비 제거 (SL2-11) */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        surgicalEquipmentService.removeEquipment(id);
        return ApiResponse.ok("장비가 제거되었습니다.", null);
    }
}
