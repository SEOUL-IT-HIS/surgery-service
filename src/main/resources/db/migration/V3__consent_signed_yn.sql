/*
 * 테이블/기능 : CONSENT (수술 동의서)
 * 작 성 일    : 2026-09-03
 * 작 성 자    : Surgery Service
 * 개    요    : 서명자·서명일을 걷어내고 동의 여부 플래그(SIGNED_YN) 하나로 바꾼다.
 *
 *               [왜 바꾸는가]
 *               동의서 원본은 종이로 받아 부서에 비치한다(§21.5). 시스템이 하는 일은
 *               "그 종이를 받았는가"를 표시하는 것뿐인데, 지금은 서명자 이름과
 *               서명일까지 다시 타이핑하게 하고 있다. 종이에 이미 적혀 있는 값을
 *               옮겨 적는 셈이라 입력만 늘고 틀릴 여지도 생긴다.
 *
 *               화면도 이 구조 때문에 폼이었다. 체크 세 번이면 끝날 일에
 *               종류·서명자·서명일을 세 번씩 채워야 했다. 체크리스트처럼
 *               체크만 하게 바꾸면서 컬럼도 거기 맞춘다.
 *
 *               [행을 지우지 않고 N 으로 두는 이유]
 *               잘못 체크했을 때 되돌릴 방법이 필요한데, 지우면 "받은 적 없음"과
 *               "받았다가 취소함"이 구분되지 않는다. 상태로 되돌린다(§21.6).
 *               CREATED_AT·UPDATED_AT 이 있어 언제 체크했는지는 남는다.
 *
 *               [SL2-217 영향]
 *               수술 시작 전 동의서 확인이 "행이 있는가"에서 "SIGNED_YN='Y' 인
 *               행이 있는가"로 바뀐다. 백엔드가 함께 수정된다.
 */

-- =============================================================================
-- 1. SIGNED_YN 추가
-- =============================================================================
ALTER TABLE CONSENT ADD (
    signed_yn  CHAR(1)  DEFAULT 'N'  NOT NULL   -- 동의서 수령 여부
);

-- 기존 행은 이미 받은 것으로 본다 — 서명자·서명일이 적혀 있다는 것은
-- 종이를 받고 옮겨 적었다는 뜻이다.
UPDATE CONSENT SET signed_yn = 'Y' WHERE signed_by IS NOT NULL;

ALTER TABLE CONSENT ADD CONSTRAINT CK_CONSENT_SIGNED_YN
    CHECK (signed_yn IN ('Y', 'N'));

COMMENT ON COLUMN CONSENT.signed_yn
    IS '동의서 수령 여부: Y수령(종이 원본 확보) / N미수령';


-- =============================================================================
-- 2. 옛 컬럼 제거
--
--    ※ 되돌릴 수 없다. 1번을 돌리고 값이 제대로 옮겨졌는지 확인한 뒤 실행할 것.
--       SELECT consent_type_cd, signed_by, signed_dt, signed_yn FROM CONSENT;
-- =============================================================================
ALTER TABLE CONSENT DROP COLUMN signed_by;
ALTER TABLE CONSENT DROP COLUMN signed_dt;


-- =============================================================================
-- 확인
-- =============================================================================
-- SELECT column_name, data_type, nullable, data_default
--   FROM user_tab_columns
--  WHERE table_name = 'CONSENT'
--  ORDER BY column_id;
--
-- SELECT signed_yn, COUNT(*) FROM CONSENT GROUP BY signed_yn;
--
-- COMMIT;
