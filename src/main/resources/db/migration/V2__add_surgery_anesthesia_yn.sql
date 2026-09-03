/*
 * 테이블/기능 : SURGERY (수술)
 * 작 성 일    : 2026-09-02
 * 작 성 자    : Surgery Service
 * 개    요    : 배정 시 마취 여부를 선언하는 ANESTHESIA_YN 컬럼 추가.
 *
 *               [왜 필요한가]
 *               배정 승인(assignOrder) 시점에 마취과 의사를 필수로 받으려 했으나,
 *               국소마취 없이 진행하는 시술(예: 단순 봉합, 표재성 종물 제거)은
 *               마취과가 붙지 않는다. 마취의를 무조건 필수로 걸면 이런 건이
 *               배정 자체가 안 되고, 반대로 계속 선택값으로 두면 "마취의를 넣는 걸
 *               잊은 것"과 "원래 마취가 없는 것"을 구분할 수 없다.
 *
 *               그래서 마취 여부를 배정자가 명시적으로 선언하게 하고,
 *               Y 일 때만 마취과 의사를 필수로 요구한다.
 *               (ANESTHESIA_YN='Y' → ANESTHESIOLOGIST_ID 필수, 서비스 계층 검증)
 *
 *               [왜 SURGERY_TYPE_CD 를 쓰지 않는가]
 *               SURGERY_TYPE_CD 는 값 정의가 정리되지 않았다 — 01전신마취 /
 *               02국소마취 / 03당일수술이 섞여 있어 마취 방식과 입원 형태가
 *               한 코드에 들어 있다. 그 정리는 별건이고, 여기서 필요한 것은
 *               "마취가 붙느냐 아니냐" 하나뿐이라 Y/N 플래그로 분리한다.
 *
 *               [기본값을 Y 로 둔 이유]
 *               기존 행과 값을 안 보내는 경로는 마취가 있는 것으로 본다.
 *               마취가 있는데 없다고 기록되는 쪽이 위험하므로, 안전한 쪽으로
 *               기울여 둔다(마취의 필수 검증이 걸리는 방향).
 *
 *               데이터타입/네이밍은 개발표준가이드 §14.2(_yn → CHAR(1))를 따른다.
 */

-- =============================================================================
-- SURGERY.ANESTHESIA_YN 추가
-- =============================================================================
ALTER TABLE SURGERY ADD (
    anesthesia_yn  CHAR(1)  DEFAULT 'Y'  NOT NULL   -- 마취 시행 여부
);

ALTER TABLE SURGERY ADD CONSTRAINT CK_SURGERY_ANESTHESIA_YN
    CHECK (anesthesia_yn IN ('Y', 'N'));

COMMENT ON COLUMN SURGERY.anesthesia_yn
    IS '마취 시행 여부: Y시행(마취과 의사 필수) / N미시행(무마취 시술)';


-- =============================================================================
-- 확인
-- =============================================================================
-- SELECT column_name, data_type, data_length, nullable, data_default
--   FROM user_tab_columns
--  WHERE table_name = 'SURGERY' AND column_name = 'ANESTHESIA_YN';
--
-- SELECT anesthesia_yn, COUNT(*) FROM SURGERY GROUP BY anesthesia_yn;
--
-- COMMIT;
