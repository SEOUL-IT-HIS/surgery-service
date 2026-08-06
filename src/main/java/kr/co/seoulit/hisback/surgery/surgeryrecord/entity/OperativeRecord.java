package kr.co.seoulit.hisback.surgery.surgeryrecord.entity;

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
 * 수술기록지 엔티티
 * <p>Oracle 물리 테이블명 OPERATIVE_RECORD. procedure_cd는 SURGERY_PROCEDURE(자체 소유
 * 업무마스터) 참조 FK, procedure_name은 코드에 없는 경우를 대비한 자유기술 값으로
 * §14.1 스냅샷 금지 규칙의 예외(그 화면에서 직접 입력하는 원본 데이터)에 해당한다.
 * Billing이 정산 시 op_status_cd='02'(확정) 건만 Pull 조회 대상으로 신뢰한다.</p>
 */
@Entity
@Table(name = "OPERATIVE_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperativeRecord {

    @Id
    @Column(name = "record_id", length = 36, nullable = false)
    private String recordId;

    // FK -> SURGERY
    @Column(name = "surgery_id", length = 36, nullable = false)
    private String surgeryId;

    // FK -> SURGERY_PROCEDURE (자체 소유 업무마스터)
    @Column(name = "procedure_cd", length = 36)
    private String procedureCd;

    // §14.1 예외: 코드에 없는 경우 대비 자유기술/보조설명 원본 입력값
    @Column(name = "procedure_name", length = 100, nullable = false)
    private String procedureName;

    // OP_STATUS_CD: 01초안(작성중)/02확정. 그룹코드는 필드명(opStatusCd)에 맞춘다.
    @Column(name = "op_status_cd", length = 20)
    private String opStatusCd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
