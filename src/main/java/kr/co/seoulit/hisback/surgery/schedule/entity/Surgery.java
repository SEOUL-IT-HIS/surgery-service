package kr.co.seoulit.hisback.surgery.schedule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 수술 엔티티 (집도의/수술실배정/시간/상태 포함)
 * <p>Oracle 물리 테이블명은 SURGERY 이다 — room/OperatingRoom과 같은 이유로
 * {@code @Table(name = "SURGERY")}를 명시한다. patient_id/surgeon_id/anesthesiologist_id/
 * nurse_id는 타 서비스(Patient/Hospital) 소유 데이터에 대한 논리참조 식별자만 저장하고,
 * 이름 등 표시값은 스냅샷으로 갖지 않는다(가이드 §14.1/§21.2) — 화면 표시 시 배치 API로 조회.</p>
 */
@Entity
@Table(name = "SURGERY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgery {

    @Id
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // 논리참조(Patient Service), 물리FK 아님
    @Column(name = "patient_id", length = 36, nullable = false)
    private String patientId;

    // 논리참조(Hospital Service), 물리FK 아님
    @Column(name = "surgeon_id", length = 36, nullable = false)
    private String surgeonId;

    @Column(name = "anesthesiologist_id", length = 36)
    private String anesthesiologistId;

    @Column(name = "nurse_id", length = 36)
    private String nurseId;

    // FK -> SURGERY_ROOM
    @Column(name = "room_code", length = 36)
    private String roomCode;

    @Column(name = "surgery_dt", nullable = false)
    private LocalDate surgeryDt;

    // SURGERY_STATUS_CD: 01예약/02진행중/03완료/04취소 (스케줄 생명주기)
    @Column(name = "status_cd", length = 36, nullable = false)
    private String statusCd;

    // SURG_PROGRESS_CD: 01대기/02진행중/03종료 (당일 실시간 진행상태, SL2-39/40, status_cd와 별개 트랙)
    @Column(name = "progress_cd", length = 36)
    private String progressCd;

    // SURGERY_CANCEL_CD: 01환자사정/02의료진사정/03응급수술우선/04기타 (status_cd=취소 시)
    @Column(name = "cancel_reason_cd", length = 36)
    private String cancelReasonCd;

    // SURG_TYPE_CD: 01전신마취/02국소마취/03당일수술
    @Column(name = "surg_type_cd", length = 36)
    private String surgTypeCd;

    @Column(name = "surgery_name", length = 100)
    private String surgeryName;

    // 프로젝트 표준 Y/N 플래그(§14.2)
    @Column(name = "emergency_yn", length = 1, nullable = false)
    private String emergencyYn;

    @Column(name = "actual_start_dt")
    private LocalDate actualStartDt;

    @Column(name = "actual_end_dt")
    private LocalDate actualEndDt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
