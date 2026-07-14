package kr.co.seoulit.hisback.surgery.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 수술장비 엔티티 (SURGICAL_EQUIPMENT)
 * <p>현재는 SL2-9(조회)/SL2-10(추가)만 구현 범위. 제거(SL2-11)·출고반입(SL2-12)·
 * 정보수정(SL2-31)은 아직 착수 전이라 관련 상태 변경 메서드는 추가하지 않았다.</p>
 */
@Getter
@Entity
@Table(name = "SURGICAL_EQUIPMENT")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurgicalEquipment {

    @Id
    @Column(name = "equipment_id", length = 36, nullable = false, updatable = false)
    private String equipmentId;

    /** 외부참조 아님: OPERATING_ROOM(내부 엔티티)에 대한 논리적 FK, 비어있을 수 있음(미배치 장비) */
    @Column(name = "room_code", length = 10)
    private String roomCode;

    @Column(name = "equipment_name", length = 100, nullable = false)
    private String equipmentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cd", length = 20, nullable = false)
    private SurgicalEquipmentStatus statusCd;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private SurgicalEquipment(String equipmentId, String roomCode, String equipmentName) {
        this.equipmentId = equipmentId;
        this.roomCode = roomCode;
        this.equipmentName = equipmentName;
        this.statusCd = SurgicalEquipmentStatus.AVAILABLE;
    }

    /** SL2-10: 수술장비 신규 등록 (PK는 UUID로 자동 생성) */
    public static SurgicalEquipment create(String roomCode, String equipmentName) {
        return new SurgicalEquipment(UUID.randomUUID().toString(), roomCode, equipmentName);
    }
}
