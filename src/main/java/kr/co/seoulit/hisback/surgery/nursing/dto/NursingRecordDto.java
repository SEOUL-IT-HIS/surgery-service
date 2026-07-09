package kr.co.seoulit.hisback.surgery.nursing.dto;

import kr.co.seoulit.hisback.surgery.nursing.entity.NursingRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술간호기록 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingRecordDto {

    private Long nursingRecordId;
    private Long surgeryId;
    private String patientPosition;
    private String disinfection;
    private String instrumentsUsed;
    private String specimenInfo;
    private String circulatingNurseId;
    private String scrubNurseId;
    private Integer countInitial;
    private Integer countFinal;
    private boolean countMatched;
    private String countRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NursingRecordDto from(NursingRecord n) {
        return NursingRecordDto.builder()
                .nursingRecordId(n.getNursingRecordId())
                .surgeryId(n.getSurgeryId())
                .patientPosition(n.getPatientPosition())
                .disinfection(n.getDisinfection())
                .instrumentsUsed(n.getInstrumentsUsed())
                .specimenInfo(n.getSpecimenInfo())
                .circulatingNurseId(n.getCirculatingNurseId())
                .scrubNurseId(n.getScrubNurseId())
                .countInitial(n.getCountInitial())
                .countFinal(n.getCountFinal())
                .countMatched(n.isCountMatched())
                .countRemark(n.getCountRemark())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }

    public NursingRecord toEntity(Long surgeryId) {
        return NursingRecord.builder()
                .surgeryId(surgeryId)
                .patientPosition(patientPosition)
                .disinfection(disinfection)
                .instrumentsUsed(instrumentsUsed)
                .specimenInfo(specimenInfo)
                .circulatingNurseId(circulatingNurseId)
                .scrubNurseId(scrubNurseId)
                .countInitial(countInitial)
                .countFinal(countFinal)
                .countMatched(countMatched)
                .countRemark(countRemark)
                .build();
    }
}
