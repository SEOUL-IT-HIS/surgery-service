package kr.co.seoulit.hisback.surgery.nursing.entity;

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
 * 수술간호기록 엔티티 (FR-SUR-008)
 * <p>데이터모델 7절 NursingRecord 참조. 물품카운트(SL2-59)·적출검체(SL2-60)를 포함한다.
 * BR-013: 물품 카운트 불일치 시 재확인 전까지 수술을 종료할 수 없다.</p>
 */
@Entity
@Table(name = "nursing_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingRecord {

    /** 수술간호기록 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nursing_record_id")
    private Long nursingRecordId;

    /** 원 수술 ID */
    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

    /** 환자 체위 */
    @Column(name = "patient_position", length = 50)
    private String patientPosition;

    /** 소독 내역 */
    @Column(name = "disinfection", length = 200)
    private String disinfection;

    /** 사용 기구 내역 */
    @Lob
    @Column(name = "instruments_used")
    private String instrumentsUsed;

    /** 적출 검체 정보 (병리과 전달 포함, SL2-60) */
    @Lob
    @Column(name = "specimen_info")
    private String specimenInfo;

    /** 순환 간호사 ID */
    @Column(name = "circulating_nurse_id", length = 20)
    private String circulatingNurseId;

    /** 기구(소독) 간호사 ID */
    @Column(name = "scrub_nurse_id", length = 20)
    private String scrubNurseId;

    /** 물품 카운트 - 시작 전 수량 */
    @Column(name = "count_initial")
    private Integer countInitial;

    /** 물품 카운트 - 종료 후 수량 */
    @Column(name = "count_final")
    private Integer countFinal;

    /** 물품 카운트 일치 여부 (SL2-59 / BR-013) */
    @Column(name = "count_matched", nullable = false)
    private boolean countMatched;

    /** 물품 카운트 비고 (불일치 시 X-ray 확인 등) */
    @Column(name = "count_remark", length = 300)
    private String countRemark;

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
