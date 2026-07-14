package kr.co.seoulit.hisback.surgery.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import kr.co.seoulit.hisback.surgery.global.converter.YnConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 수술 엔티티 (집도의/수술실배정/시간/상태 포함)
 * <p>데이터모델 7절 Surgery 참조. 개발표준가이드 §14 기준: PK는 애플리케이션에서
 * 생성하는 UUID(VARCHAR2(36)), 테이블명 UPPER_SNAKE_CASE, 상태 컬럼은 _cd 접미사,
 * 시각까지 필요한 컬럼은 _dt가 아닌 _at(TIMESTAMP)을 쓴다.</p>
 */
@Entity
@Table(name = "SURGERY")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgery {

    /** 수술 고유번호 (앱에서 생성하는 UUID) */
    @Id
    @Column(name = "surgery_id", length = 36, updatable = false)
    private String surgeryId;

    /** 환자 마스터 식별자(MPI) - 환자관리 서비스 소유 데이터의 논리참조, DB FK 없음 */
    @Column(name = "patient_mpi_id", length = 36, nullable = false)
    private String patientMpiId;

    /** 수술실 코드 - OPERATING_ROOM.room_code FK (내부 엔티티) */
    @Column(name = "room_code", length = 10)
    private String roomCode;

    /** 집도의 ID - 병원관리 서비스 소유 데이터의 논리참조, DB FK 없음 */
    @Column(name = "surgeon_id", length = 36)
    private String surgeonId;

    /** 마취의 ID - 병원관리 서비스 소유 데이터의 논리참조, DB FK 없음 */
    @Column(name = "anesthesiologist_id", length = 36)
    private String anesthesiologistId;

    /** 수술명 */
    @Column(name = "surgery_name", length = 200)
    private String surgeryName;

    /** 수술 예정일시 (날짜만이 아니라 시각까지 필요해 _at/TIMESTAMP) */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /** 수술상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_cd", length = 20, nullable = false)
    private SurgeryStatus status;

    /** 응급 수술 여부 (SL2-44) - CHAR(1) 'Y'/'N' */
    @Convert(converter = YnConverter.class)
    @Column(name = "emergency_yn", length = 1, nullable = false)
    private boolean emergency;

    /** 실제 수술 시작일시 (턴오버 타임 계산용) */
    @Column(name = "actual_start_at")
    private LocalDateTime actualStartAt;

    /** 실제 수술 종료일시 */
    @Column(name = "actual_end_at")
    private LocalDateTime actualEndAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.surgeryId == null) {
            this.surgeryId = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = SurgeryStatus.SCHEDULED;
        }
    }
}
