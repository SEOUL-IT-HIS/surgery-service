package kr.co.seoulit.hisback.surgery.checklist.entity;

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
 * 수술안전체크리스트 엔티티
 * <p>Oracle 물리 테이블명 SURGERY_CHECKLIST. SignIn -&gt; TimeOut -&gt; SignOut 순서 검증은
 * 애플리케이션(서비스) 레벨에서 수행한다 (DB 레벨 제약 없음).</p>
 */
@Entity
@Table(name = "SURGERY_CHECKLIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryChecklist {

    @Id
    @Column(name = "checklist_id", length = 36, nullable = false)
    private String checklistId;

    // FK -> SURGERY
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // CHECKLIST_STEP_CD: 01=SignIn/02=TimeOut/03=SignOut
    @Column(name = "phase_cd", length = 36, nullable = false)
    private String phaseCd;

    // 프로젝트 표준 Y/N 플래그(§14.2)
    @Column(name = "completed_yn", length = 1, nullable = false)
    private String completedYn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
