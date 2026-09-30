/*
 * 테이블/기능 : SURGERY_ORDER, SURGERY (수술 오더 상세정보)
 * 개요         : 수술 목적 및 필요한 처치 요청을 담는 자유 텍스트 DETAIL_INFO 추가.
 *                오더 접수 시 저장하고, 배정 승인으로 SURGERY 를 생성할 때 함께 전달한다.
 */

ALTER TABLE SURGERY_ORDER ADD (
    detail_info CLOB
);

ALTER TABLE SURGERY ADD (
    detail_info CLOB
);

COMMENT ON COLUMN SURGERY_ORDER.detail_info
    IS '수술 목적 및 필요한 처치 요청 (자유 텍스트)';

COMMENT ON COLUMN SURGERY.detail_info
    IS '요청된 수술 목적 및 필요한 처치 내용 (자유 텍스트)';
