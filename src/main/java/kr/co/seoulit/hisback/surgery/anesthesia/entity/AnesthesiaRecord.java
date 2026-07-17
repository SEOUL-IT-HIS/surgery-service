package kr.co.seoulit.hisback.surgery.anesthesia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
 * 마취기록 엔티티 (활력징후, 약물투여 포함)
 * <p>Oracle 물리 테이블명 ANESTHESIA_RECORD. vital_signs_log는 CLOB — 활력징후/약물투여
 * 로그를 구조화된 텍스트(JSON 등)로 이어붙이는 방식이라 별도 컬럼화하지 않는다.</p>
 */
@Entity
@Table(name = "ANESTHESIA_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnesthesiaRecord {

    @Id
    @Column(name = "anesthesia_id", length = 36, nullable = false)
    private String anesthesiaId;

    // FK -> SURGERY
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // ANESTHESIA_TYPE_CD: 01전신마취/02척추마취/03국소마취/04기타
    @Column(name = "anesthesia_type_cd", length = 36)
    private String anesthesiaTypeCd;

    // ASA_CD: 01~06 (ASA1~ASA6, SL2-45 마취전평가)
    @Column(name = "asa_grade_cd", length = 36)
    private String asaGradeCd;

    @Lob
    @Column(name = "vital_signs_log")
    private String vitalSignsLog;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
