package kr.co.seoulit.hisback.surgery.operation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 수술기록지(Operative Note) 엔티티 (FR-SUR-007)
 * <p>데이터모델 7절 OperativeRecord 참조. BR-014: 수술 종료 후 24시간 이내 확정.</p>
 */
@Entity
@Table(name = "operative_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperativeRecord {

    /** 수술기록지 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    /** 원 수술 ID */
    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

    /** 수술명/술식명 */
    @Column(name = "procedure_name", length = 200)
    private String procedureName;

    /** 술식 상세 */
    @Lob
    @Column(name = "procedure_detail")
    private String procedureDetail;

    /** 수술 소견 */
    @Lob
    @Column(name = "findings")
    private String findings;

    /** 수술 후 진단 */
    @Column(name = "postoperative_diagnosis", length = 300)
    private String postoperativeDiagnosis;

    /** 실혈량(ml) */
    @Column(name = "blood_loss_ml")
    private Integer bloodLossMl;

    /** 집도의(작성자) ID */
    @Column(name = "surgeon_id", length = 20)
    private String surgeonId;

    /** 확정 여부 (임시저장 → 확정) */
    @Column(name = "finalized_yn", nullable = false)
    private boolean finalized;

    /** 확정 일시 (BR-014) */
    @Column(name = "finalized_dt")
    private LocalDateTime finalizedDt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
