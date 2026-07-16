package kr.co.seoulit.hisback.surgery.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수술장비 DTO
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgicalEquipmentDto {
    private String equipment_name;
    private int status_cd;
    private String inout_cd;
}
