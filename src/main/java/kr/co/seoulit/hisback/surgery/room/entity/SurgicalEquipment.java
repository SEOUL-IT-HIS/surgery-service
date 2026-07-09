package kr.co.seoulit.hisback.surgery.room.entity;

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
 * 수술장비 엔티티 (FR-SUR-001)
 * <p>출고/반입 이력(SL2-12)은 상태와 최근 이동정보로 관리한다.</p>
 */
@Entity
@Table(name = "surgical_equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgicalEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 장비 코드 (업무 키, 유니크) */
    @Column(name = "equipment_code", length = 20, nullable = false, unique = true)
    private String equipmentCode;

    /** 장비 명칭 */
    @Column(name = "equipment_name", length = 100)
    private String equipmentName;

    /** 장비 분류 */
    @Column(name = "category", length = 30)
    private String category;

    /** 배치된 수술방 코드 (미배치 시 null) */
    @Column(name = "operating_room", length = 10)
    private String operatingRoom;

    /** 장비 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EquipmentStatus status;

    /** 최근 이동 유형 (OUT: 출고 / IN: 반입) */
    @Column(name = "last_movement_type", length = 10)
    private String lastMovementType;

    /** 최근 이동 일시 */
    @Column(name = "last_movement_dt")
    private LocalDateTime lastMovementDt;

    /** 사용 여부 (제거 시 false 로 소프트 삭제) */
    @Column(name = "active_yn", nullable = false)
    private boolean active;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) {
            this.status = EquipmentStatus.AVAILABLE;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
