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
 * 수술실 엔티티
 * <p>Oracle 물리 테이블명은 SURGERY_ROOM 이다. Java 클래스명(OperatingRoom)과 테이블명이 서로
 * 다르므로 {@code @Table(name = "SURGERY_ROOM")}으로 명시 매핑해야 한다 — 이걸 생략하면
 * Hibernate가 클래스명을 스네이크케이스로 변환한 "operating_room"이라는 별개 테이블을 찾고,
 * application.properties의 {@code spring.jpa.hibernate.ddl-auto=update} 설정 때문에
 * 없는 테이블을 새로 만들어버려 기존 SURGERY_ROOM 데이터와 완전히 어긋나게 된다.</p>
 */

@Entity
@Table(name = "SURGERY_ROOM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatingRoom {

    // PK(room_code)는 서버가 채번하지 않고 클라이언트에서 지정한 코드를 그대로 저장한다
    @Id
    @Column(name = "room_code", length = 36, nullable = false)
    private String roomCode;

    @Column(name = "room_name", length = 100, nullable = false)
    private String roomName;

    // OR_STATUS_CD: 01사용가능/02사용중/03점검중/04폐쇄
    @Column(name = "status_cd", length = 36)
    private String statusCd;

    // TURNOVER_CD: 01정리중/02준비완료 (SL2-50 턴오버타임 관리)
    @Column(name = "turnover_cd", length = 36)
    private String turnoverCd;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
