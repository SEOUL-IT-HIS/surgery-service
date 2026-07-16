/*
 * 테이블/기능 : SURGERY_ROOM, SURGERY_EQUIPMENT (수술실/수술장비)
 * 작 성 일    : 2026-07-16
 * 작 성 자    : Surgery Service
 * 개    요    : 수술실·수술장비 마스터 테이블 생성.
 *               JPA 엔티티(OperatingRoom, SurgicalEquipment) 매핑과 1:1로 일치시킨다.
 *               데이터타입/네이밍은 개발표준가이드 §14(Oracle DB 규칙)를 따른다.
 */

-- =============================================================================
-- SURGERY_ROOM : 수술실 (엔티티 OperatingRoom, PK = room_code)
-- =============================================================================
CREATE TABLE SURGERY_ROOM (
    room_code    VARCHAR2(36)   NOT NULL,                        -- 수술실 코드(클라이언트 지정 PK)
    room_name    VARCHAR2(100)  NOT NULL,                        -- 수술실 명칭
    status_cd    VARCHAR2(36),                                   -- 수술실 상태 코드
    turnover_cd  VARCHAR2(36),                                   -- 턴오버(정리) 상태 코드
    created_at   TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,   -- 생성 일시
    updated_at   TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,   -- 수정 일시
    CONSTRAINT PK_SURGERY_ROOM PRIMARY KEY (room_code)
);

COMMENT ON TABLE  SURGERY_ROOM             IS '수술실 마스터';
COMMENT ON COLUMN SURGERY_ROOM.room_code   IS '수술실 코드 (PK, 클라이언트 지정)';
COMMENT ON COLUMN SURGERY_ROOM.room_name   IS '수술실 명칭';
COMMENT ON COLUMN SURGERY_ROOM.status_cd   IS '수술실 상태 코드: 01사용가능 / 02사용중 / 03점검중 / 04폐쇄';
COMMENT ON COLUMN SURGERY_ROOM.turnover_cd IS '턴오버 상태 코드(SL2-50): 01정리중 / 02준비완료';
COMMENT ON COLUMN SURGERY_ROOM.created_at  IS '생성 일시';
COMMENT ON COLUMN SURGERY_ROOM.updated_at  IS '수정 일시';

-- =============================================================================
-- SURGERY_EQUIPMENT : 수술장비 (엔티티 SurgicalEquipment, PK = equipment_id)
-- =============================================================================
CREATE TABLE SURGERY_EQUIPMENT (
    equipment_id    VARCHAR2(36)   NOT NULL,                        -- 수술장비 ID(클라이언트 지정 PK)
    equipment_name  VARCHAR2(100)  NOT NULL,                        -- 수술장비 명칭
    status_cd       VARCHAR2(36),                                   -- 장비 상태 코드
    inout_cd        VARCHAR2(36),                                   -- 출고반입 상태 코드
    created_at      TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,   -- 생성 일시
    updated_at      TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,   -- 수정 일시
    CONSTRAINT PK_SURGERY_EQUIPMENT PRIMARY KEY (equipment_id)
);

COMMENT ON TABLE  SURGERY_EQUIPMENT                IS '수술장비 마스터';
COMMENT ON COLUMN SURGERY_EQUIPMENT.equipment_id   IS '수술장비 ID (PK, 클라이언트 지정)';
COMMENT ON COLUMN SURGERY_EQUIPMENT.equipment_name IS '수술장비 명칭';
COMMENT ON COLUMN SURGERY_EQUIPMENT.status_cd      IS '장비 상태 코드';
COMMENT ON COLUMN SURGERY_EQUIPMENT.inout_cd       IS '출고반입 상태 코드(SL2-12)';
COMMENT ON COLUMN SURGERY_EQUIPMENT.created_at     IS '생성 일시';
COMMENT ON COLUMN SURGERY_EQUIPMENT.updated_at     IS '수정 일시';