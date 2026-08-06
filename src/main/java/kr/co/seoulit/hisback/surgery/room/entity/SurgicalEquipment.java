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
 * <p>OperatingRoom(SURGERY_ROOM)과 동일한 매핑 규약을 따른다. DDL(surgery_service_ddl_v4.sql)의
 * 실제 물리 테이블명은 SURGICAL_EQUIPMENT 이다 — {@code @Table(name = "SURGERY_EQUIPMENT")}로
 * 매핑돼 있으면 이름이 한 글자 다르므로(SURGERY_EQUIPMENT vs SURGICAL_EQUIPMENT) OperatingRoom과
 * 똑같은 문제가 재발한다: Hibernate가 엉뚱한 테이블을 새로 만들어버린다.</p>
 */

@Entity
@Table(name = "SURGICAL_EQUIPMENT")
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

    // FK -> SURGERY_ROOM.room_code (DDL상 NOT NULL). 어느 수술실 소속 장비인지 나타낸다.
    @Column(name = "room_code", length = 36, nullable = false)
    private String roomCode;

    @Column(name = "equipment_name", length = 100, nullable = false)
    private String equipmentName;

    // OR_EQUIP_STATUS_CD: 01사용가능/02사용중/03점검중/04고장
    @Column(name = "status_cd", length = 36)
    private String statusCd;

    // EQUIP_INOUT_CD: 01출고/02반입 (SL2-12 출고반입관리)
    @Column(name = "inout_cd", length = 36)
    private String inoutCd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
