package kr.co.seoulit.hisback.surgery.proceduremaster.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 수술항목 마스터 엔티티 (Surgery Service 소유 업무마스터)
 */

@Entity
@Table(name = "SURGERY_PROCEDURE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryProcedure {

    @Id
    @Column(name = "PROCEDURE_CD", length = 36, nullable = false)
    private String procedureCd;

    @Column(name = "PROCEDURE_NAME", length = 100, nullable = false)
    private String procedureName;

    @Column(name = "ACTIVE_YN", length = 1, nullable = false)
    private String activeYn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
