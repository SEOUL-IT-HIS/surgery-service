package kr.co.seoulit.hisback.surgery.nursingrecord.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 수술간호기록 엔티티 (물품카운트/적출검체 포함)
 * <p>Oracle 물리 테이블명 NURSING_RECORD.</p>
 */
@Entity
@Table(name = "NURSING_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingRecord {

    @Id
    @Column(name = "nursing_record_id", length = 36, nullable = false)
    private String nursingRecordId;

    // FK -> SURGERY
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // COUNT_RESULT_CD: 01일치/02불일치
    @Column(name = "item_count_result_cd", length = 36)
    private String itemCountResultCd;

    @Column(name = "specimen_barcode", length = 36)
    private String specimenBarcode;

    // SPECIMEN_OR_CD: 01조직/02이물/03기타
    @Column(name = "specimen_type_cd", length = 36)
    private String specimenTypeCd;

    // OR_NURSE_NOTE_CD: 01작성중/02완료
    @Column(name = "record_status_cd", length = 36)
    private String recordStatusCd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
