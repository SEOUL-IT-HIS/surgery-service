package kr.co.seoulit.hisback.surgery.checklist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * 수술안전체크리스트 엔티티
 * <p>데이터모델 7절 SurgeryChecklist 참조. 수술 1건당 단계(phase)별 1건을 갖는다.</p>
 */
@Entity
@Table(name = "surgery_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryChecklist {

    /** 체크리스트 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long checklistId;

    /** 원 수술 ID */
    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

    /** 단계(SignIn/TimeOut/SignOut) */
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", length = 10, nullable = false)
    private ChecklistPhase phase;

    /** 항목별 체크 결과 (JSON 텍스트) */
    @Lob
    @Column(name = "items")
    private String items;

    /** 완료 여부 */
    @Column(name = "completed_yn", nullable = false)
    private boolean completedYn;

    /** 작성자 ID */
    @Column(name = "checked_by", length = 20)
    private String checkedBy;

    /** 작성/확정 일시 */
    @Column(name = "checked_dt")
    private LocalDateTime checkedDt;

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
