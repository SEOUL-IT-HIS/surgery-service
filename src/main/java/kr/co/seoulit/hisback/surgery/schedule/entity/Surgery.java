package kr.co.seoulit.hisback.surgery.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * 수술 엔티티 (집도의/수술실배정/시간/상태 포함)
 * <p>데이터모델 7절 Surgery 참조.</p>
 */
@Entity
@Table(name = "surgery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgery {

    /** 수술 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surgery_id")
    private Long surgeryId;

    /** 환자 마스터 식별자(MPI) */
    @Column(name = "patient_mpi_id", length = 20, nullable = false)
    private String patientMpiId;

    /** 수술방 코드 */
    @Column(name = "operating_room", length = 10)
    private String operatingRoom;

    /** 집도의 ID */
    @Column(name = "surgeon_id", length = 20)
    private String surgeonId;

    /** 마취의 ID */
    @Column(name = "anesthesiologist_id", length = 20)
    private String anesthesiologistId;

    /** 수술명 */
    @Column(name = "surgery_name", length = 200)
    private String surgeryName;

    /** 수술 예정일시 */
    @Column(name = "scheduled_dt")
    private LocalDateTime scheduledDt;

    /** 수술상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SurgeryStatus status;

    /** 응급 수술 여부 (SL2-44) */
    @Column(name = "emergency_yn", nullable = false)
    private boolean emergency;

    /** 실제 수술 시작일시 (턴오버 타임 계산용) */
    @Column(name = "actual_start_dt")
    private LocalDateTime actualStartDt;

    /** 실제 수술 종료일시 */
    @Column(name = "actual_end_dt")
    private LocalDateTime actualEndDt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) {
            this.status = SurgeryStatus.SCHEDULED;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
