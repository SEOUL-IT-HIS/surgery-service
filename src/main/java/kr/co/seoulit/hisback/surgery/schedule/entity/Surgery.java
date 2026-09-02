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

    // SURGERY_STATUS_CD: 00요청접수/01예약/02진행중/03완료/04취소 (스케줄 생명주기)
    // 값은 SurgeryStatus 상수로 다룬다 — 문자열을 코드 곳곳에 흩어놓지 않는다.
    @Column(name = "status_cd", length = 36, nullable = false)
    private String statusCd;

    // SURGERY_PROGRESS_CD: 01대기/02진행중/03종료 (당일 실시간 진행상태, SL2-39/40, status_cd와 별개 트랙)
    @Column(name = "progress_cd", length = 36)
    private String progressCd;

    // SURGERY_CANCEL_CD: 01환자사정/02의료진사정/03응급수술우선/04기타 (status_cd=취소 시)
    @Column(name = "cancel_reason_cd", length = 36)
    private String cancelReasonCd;

    // SURGERY_TYPE_CD
    // TODO: 값 정의가 잘못돼 있다 — 01전신마취/02국소마취/03당일수술은 마취 방식과 입원 형태가
    //       한 코드에 섞인 것이다. 마취 방식은 ANESTHESIA_TYPE_CD 소관이므로,
    //       공통코드 등록 전에 이 필드가 무엇을 담을지 다시 정해야 한다.
    @Column(name = "surgery_type_cd", length = 36)
    private String surgeryTypeCd;

    @Column(name = "surgery_name", length = 100)
    private String surgeryName;

    // 프로젝트 표준 Y/N 플래그(§14.2)
    @Column(name = "emergency_yn", length = 1, nullable = false)
    private String emergencyYn;

    // 마취 시행 여부: Y시행 / N미시행(무마취 시술)
    //
    // surgery_type_cd 로 대신하지 않는 이유는 바로 위 TODO 때문이다 — 그 코드는
    // 마취 방식과 입원 형태가 섞여 있어 "마취가 붙느냐"를 물을 수 없다.
    //
    // 이 값이 Y 면 배정 시 마취과 의사가 필수다(SurgeryOrderServiceImpl.assignOrder).
    // N 은 국소마취 없이 하는 시술 — 단순 봉합, 표재성 종물 제거 같은 것들이다.
    // 선택값으로 두지 않은 이유는, 그러면 "마취의를 넣는 걸 잊은 것"과
    // "원래 마취가 없는 것"이 DB 에서 똑같이 NULL 로 보이기 때문이다.
    @Column(name = "anesthesia_yn", length = 1, nullable = false)
    private String anesthesiaYn;

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
