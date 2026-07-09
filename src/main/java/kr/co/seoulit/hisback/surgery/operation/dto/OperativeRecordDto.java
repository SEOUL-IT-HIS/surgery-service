package kr.co.seoulit.hisback.surgery.operation.dto;

import kr.co.seoulit.hisback.surgery.operation.entity.OperativeRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술기록지 DTO (요청/응답 공용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperativeRecordDto {

    private Long recordId;
    private Long surgeryId;
    private String procedureName;
    private String procedureDetail;
    private String findings;
    private String postoperativeDiagnosis;
    private Integer bloodLossMl;
    private String surgeonId;
    private boolean finalized;
    private LocalDateTime finalizedDt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OperativeRecordDto from(OperativeRecord r) {
        return OperativeRecordDto.builder()
                .recordId(r.getRecordId())
                .surgeryId(r.getSurgeryId())
                .procedureName(r.getProcedureName())
                .procedureDetail(r.getProcedureDetail())
                .findings(r.getFindings())
                .postoperativeDiagnosis(r.getPostoperativeDiagnosis())
                .bloodLossMl(r.getBloodLossMl())
                .surgeonId(r.getSurgeonId())
                .finalized(r.isFinalized())
                .finalizedDt(r.getFinalizedDt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    public OperativeRecord toEntity(Long surgeryId) {
        return OperativeRecord.builder()
                .surgeryId(surgeryId)
                .procedureName(procedureName)
                .procedureDetail(procedureDetail)
                .findings(findings)
                .postoperativeDiagnosis(postoperativeDiagnosis)
                .bloodLossMl(bloodLossMl)
                .surgeonId(surgeonId)
                .finalized(finalized)
                .build();
    }
}
