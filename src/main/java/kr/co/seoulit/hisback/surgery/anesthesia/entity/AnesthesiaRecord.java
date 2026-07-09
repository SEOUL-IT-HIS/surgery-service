package kr.co.seoulit.hisback.surgery.anesthesia.entity;

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
 * 마취기록 엔티티 (활력징후, 약물투여 포함)
 * <p>데이터모델 7절 AnesthesiaRecord 참조. (SL2-18 활력징후 / SL2-21 약물투여 / SL2-45 마취 전 평가)</p>
 */
@Entity
@Table(name = "anesthesia_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaRecord {

    /** 마취기록 고유번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anesthesia_id")
    private Long anesthesiaId;

    /** 원 수술 ID */
    @Column(name = "surgery_id", nullable = false)
    private Long surgeryId;

    /** 마취 유형(전신/부위/국소 등) */
    @Column(name = "anesthesia_type", length = 30)
    private String anesthesiaType;

    /** 마취 전 평가 (SL2-45) */
    @Lob
    @Column(name = "pre_anesthesia_eval")
    private String preAnesthesiaEval;

    /** 마취 전·중·후 활력징후 기록 (JSON 텍스트, SL2-18) */
    @Lob
    @Column(name = "vital_signs_log")
    private String vitalSignsLog;

    /** 마취약물 투여 내역 (JSON 텍스트, SL2-21) */
    @Lob
    @Column(name = "drug_log")
    private String drugLog;

    /** 기록 작성자(마취의) ID */
    @Column(name = "recorded_by", length = 20)
    private String recordedBy;

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
