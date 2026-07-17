package kr.co.seoulit.hisback.surgery.room.entity;

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
 * 수술장비 엔티티
 * <p>OperatingRoom(SURGERY_ROOM)과 동일한 매핑 규약을 따른다. 물리 테이블명을 명시하지 않으면
 * Hibernate가 클래스명을 스네이크케이스로 변환한 별개 테이블(surgical_equipment)을 만들 수 있고,
 * {@code spring.jpa.hibernate.ddl-auto=update} 설정 탓에 의도치 않은 테이블이 생성되므로
 * {@code @Table(name = "SURGERY_EQUIPMENT")}로 명시 매핑한다.</p>
 */

@Entity
@Table(name = "SURGERY_EQUIPMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgicalEquipment {

    // PK(equipment_id)는 서버가 채번하지 않고, 호출자(프론트)가 지정한 코드를 그대로 저장한다
    @Id
    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "equipment_name", length = 100, nullable = false)
    private String equipmentName;

    // STATUS_CD: 장비 상태 코드
    @Column(name = "status_cd", length = 36)
    private String statusCd;

    // INOUT_CD: 출고/반입 상태 코드 (SL2-12 출고반입관리)
    @Column(name = "inout_cd", length = 36)
    private String inoutCd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
