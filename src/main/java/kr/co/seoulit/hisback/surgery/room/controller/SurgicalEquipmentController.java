package kr.co.seoulit.hisback.surgery.room.controller;

import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.global.common.PageResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.service.SurgicalEquipmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //getEquipments는 GET 전체 요청을 받아 페이지 단위로 SurgicalEquipmentService에 전달(위임)하고,
    //Service에서 가져온 결과물을 ApiResponse로 감싸 반환한다 (SL2-110: page/size/sort)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SurgicalEquipmentDto>>> getEquipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        Pageable pageable = (sort != null && !sort.isBlank())
                ? PageRequest.of(page, size, Sort.by(sort))
                : PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(surgicalEquipmentService.getSurgicalEquipments(pageable)));
    }

    //getEquipment는 특정 장비 ID로 특정 장비를 조회한다
    @GetMapping("/{equipmentId}")
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> getEquipment(@PathVariable String equipmentId){
        return ResponseEntity.ok(
                ApiResponse.success(surgicalEquipmentService.getSurgicalEquipmentfindById(equipmentId))
        );
    }

    //createEquipment는 POST 요청을 받아 SurgicalEquipmentService에 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    @PostMapping
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> createEquipment(@RequestBody SurgicalEquipmentDto equipmentDto) {
        SurgicalEquipmentDto created = surgicalEquipmentService.createSurgicalEquipment(equipmentDto);
        return ResponseEntity.status(201).body(ApiResponse.success(201, created));
    }

    //updateEquipment는 PUT 요청을 받아 SurgicalEquipmentService에 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
    @PutMapping("/{equipmentId}")
    public ResponseEntity<ApiResponse<SurgicalEquipmentDto>> updateEquipment(@PathVariable String equipmentId, @RequestBody SurgicalEquipmentDto equipmentDto) {
        SurgicalEquipmentDto updated = surgicalEquipmentService.updateSurgicalEquipment(equipmentId, equipmentDto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    //deleteEquipment는 DELETE 요청을 받아 SurgicalEquipmentService에 전달(위임)하고, Service에서 받아온 결과를 ApiResponse로 감싸 반환한다
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