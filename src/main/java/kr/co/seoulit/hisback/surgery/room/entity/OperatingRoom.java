package kr.co.seoulit.hisback.surgery.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.co.seoulit.hisback.surgery.global.exception.BusinessException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 수술실 엔티티 (OPERATING_ROOM)
 * <p>PK는 업무상 코드값(room_code)을 그대로 사용한다.</p>
 */
@Getter
@Entity
@Table(name = "OPERATING_ROOM")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingRoom {

    @Id
    @Column(name = "room_code", length = 10, nullable = false, updatable = false)
    private String roomCode;

    @Column(name = "room_name", length = 100, nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cd", length = 20, nullable = false)
    private OperatingRoomStatus statusCd;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private OperatingRoom(String roomCode, String roomName) {
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.statusCd = OperatingRoomStatus.AVAILABLE;
    }

    /** SL2-7: 수술실 신규 등록 */
    public static OperatingRoom create(String roomCode, String roomName) {
        return new OperatingRoom(roomCode, roomName);
    }

    /** SL2-30: 수술실 정보 수정 (이름만 변경 대상, 상태는 별도 API로 관리) */
    public void updateInfo(String roomName) {
        this.roomName = roomName;
    }

    /**
     * SL2-8: 수술실 제거
     * <p>물리 삭제가 아닌 CLOSED 상태로 전이한다. 이미 폐쇄된 수술실은 다시 폐쇄할 수 없다.</p>
     */
    public void changeStatus(OperatingRoomStatus newStatus) {
        if (this.statusCd == OperatingRoomStatus.CLOSED && newStatus == OperatingRoomStatus.CLOSED) {
            throw BusinessException.conflict("이미 제거(폐쇄)된 수술실입니다.");
        }
        this.statusCd = newStatus;
    }
}
