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
 * 수술실 엔티티 (FR-SUR-001)
 * <p>{@code roomCode}가 수술(Surgery.operatingRoom)에서 참조하는 업무 키이다.</p>
 */
@Entity
@Table(name = "operating_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 수술방 코드 (업무 키, 유니크) */
    @Column(name = "room_code", length = 10, nullable = false, unique = true)
    private String roomCode;

    /** 수술방 명칭 */
    @Column(name = "room_name", length = 50)
    private String roomName;

    /** 수술방 유형 (일반/특수/하이브리드 등) */
    @Column(name = "room_type", length = 30)
    private String roomType;

    /** 수술방 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private RoomStatus status;

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
            this.status = RoomStatus.AVAILABLE;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
