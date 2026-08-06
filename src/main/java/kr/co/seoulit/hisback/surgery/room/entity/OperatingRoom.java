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

// ─────────────────────────────────────────────────────────────────────────────
// 이 파일은 엔티티 계층의 '대표 예제'다. 어노테이션마다 설명을 달아두었으니
// 다른 엔티티(SurgicalEquipment, Surgery, Consent ...)는 같은 모양으로 읽으면 된다.
// ─────────────────────────────────────────────────────────────────────────────

// @Entity — 이 클래스를 DB 테이블과 짝지어 관리하라고 JPA에게 알린다.
//           이게 있어야 Repository 가 이 타입을 다룰 수 있다. 없으면 그냥 평범한 자바 클래스다.
@Entity
// @Table — 짝지을 물리 테이블명을 직접 지정한다.
//          생략하면 Hibernate 가 클래스명을 스네이크케이스로 바꿔(operating_room) 찾는데,
//          실제 테이블은 SURGERY_ROOM 이라 어긋난다(위 클래스 주석 참고).
@Table(name = "SURGERY_ROOM")
// @Getter / @Setter — Lombok 이 getRoomCode() / setRoomCode() 같은 메서드를 컴파일 시점에 만들어준다.
//                     직접 안 적어도 되지만, 코드에서는 있는 것처럼 호출하면 된다.
@Getter
@Setter
// @NoArgsConstructor — 인자 없는 생성자. JPA 가 DB에서 읽은 행으로 객체를 만들 때
//                      빈 객체를 먼저 생성하고 값을 채우는 방식이라 반드시 필요하다.
@NoArgsConstructor
// @AllArgsConstructor — 모든 필드를 받는 생성자. @Builder 가 내부적으로 사용한다.
@AllArgsConstructor
// @Builder — OperatingRoom.builder().roomCode("OR01").roomName("제1수술실").build() 형태로
//            객체를 만들 수 있게 한다. 필드가 많을 때 순서를 헷갈리지 않아 안전하다.
@Builder
public class OperatingRoom {

    // @Id — 이 필드가 기본키(PK)임을 표시한다. 엔티티마다 반드시 하나 있어야 한다.
    //       @GeneratedValue 를 붙이지 않은 이유: PK(room_code)를 서버가 채번하지 않고
    //       클라이언트가 지정한 코드('OR01')를 그대로 저장하기 때문이다.
    @Id
    // @Column — 짝지을 컬럼명과 제약을 지정한다.
    //   name      : DB 컬럼명. 자바는 camelCase, DB는 lower_snake 라 변환이 필요하다(§13).
    //   length    : VARCHAR2 길이. `_cd`/`_id` 는 36 을 쓴다(§14.2).
    //   nullable  : false 면 NOT NULL. DDL 생성과 저장 전 검증에 함께 쓰인다.
    @Column(name = "room_code", length = 36, nullable = false)
    private String roomCode;

    @Column(name = "room_name", length = 100, nullable = false)
    private String roomName;

    // OR_STATUS_CD: 01사용가능/02사용중/03점검중/04폐쇄
    // nullable 을 적지 않아 기본값(true)이다 — 등록 직후에는 상태가 비어 있을 수 있다.
    @Column(name = "status_cd", length = 36)
    private String statusCd;

    // OR_TURNOVER_CD: 01정리중/02준비완료 (SL2-50 턴오버타임 관리)
    @Column(name = "turnover_cd", length = 36)
    private String turnoverCd;

    // @CreationTimestamp — INSERT 되는 순간 현재 시각을 자동으로 넣는다(Hibernate 기능).
    //                      코드에서 setCreatedAt() 을 부를 필요가 없다.
    @CreationTimestamp
    // updatable = false — 한 번 들어간 생성 시각은 이후 UPDATE 문에 포함되지 않는다.
    //                     실수로 덮어써도 DB 값은 지켜진다.
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // @UpdateTimestamp — UPDATE 될 때마다 현재 시각으로 갱신한다.
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
