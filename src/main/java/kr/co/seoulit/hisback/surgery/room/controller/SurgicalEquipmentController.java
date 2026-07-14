package kr.co.seoulit.hisback.surgery.room.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import kr.co.seoulit.hisback.surgery.room.dto.SurgicalEquipmentDto;
import kr.co.seoulit.hisback.surgery.room.service.SurgicalEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수술장비 관리 컨트롤러
 * <p>현재 SL2-9(조회)/SL2-10(추가)만 구현. SL2-11(제거)/SL2-12(출고반입관리)/
 * SL2-31(정보수정)은 지라 상태가 "해야 할 일"이라 이번 범위에서 제외했다.</p>
 */
@RestController
@RequestMapping("/api/v1/surgery/equipment")
@RequiredArgsConstructor
public class SurgicalEquipmentController {

    private final SurgicalEquipmentService surgicalEquipmentService;

    /** SL2-9: 수술장비 목록 조회 */
    @GetMapping
    public ApiResponse<List<SurgicalEquipmentDto.Response>> getEquipments() {
        return ApiResponse.ok(surgicalEquipmentService.getEquipments());
    }

    /** SL2-10: 수술장비 추가 */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<SurgicalEquipmentDto.Response> createEquipment(
            @Valid @RequestBody SurgicalEquipmentDto.CreateRequest request) {
        return ApiResponse.ok(surgicalEquipmentService.createEquipment(request));
    }
}
