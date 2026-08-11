package kr.co.seoulit.hisback.surgery.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 공통 에러 코드(수술)
 * <p>{@code messageCode} 는 프론트 features/surgery/messages.ts(SURxxx) 매핑 키다.
 * GlobalExceptionHandler 가 응답 message 에 이 코드를 실어 보내고, 프론트가 딕셔너리로 문구를 변환한다(§15.2).
 * {@code message} 는 서버 기본 문구(로그·폴백용).</p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SURGERY_NOT_FOUND(404, "SUR035", "해당 수술이 존재하지 않습니다"),
    SURGERY_ROOM_NOT_FOUND(404, "SUR036", "해당 수술실이 존재하지 않습니다"),
    EQUIPMENT_NOT_FOUND(404, "SUR037", "해당 장비가 존재하지 않습니다"),
    INVALID_REQUEST(400, "SUR038", "잘못된 요청입니다"),
    INVALID_SURGERY_STATUS(400, "SUR039", "잘못된 수술 상태 값 또는 전이입니다"),
    INTERNAL_SERVER_ERROR(500, "SUR040", "서버 내부 오류"),
    // SUR041 은 프론트에서 장비 삭제 실패 문구로 이미 점유 중이므로 비워 둔다(messages.ts).
    OPERATIVE_RECORD_NOT_FOUND(404, "SUR042", "해당 수술기록이 존재하지 않습니다"),
    OPERATIVE_RECORD_ALREADY_FIXED(400, "SUR043", "확정된 수술기록은 수정할 수 없습니다"),
    CONSENT_IS_INSERT_ONE_TO_ONE(400, "SUR044", "수술 동의서는 1:1로만 등록할 수 있습니다"),
    SURGERY_ROOM_NOT_AVAILABLE(400, "SUR045", "점검중이거나 폐쇄된 수술실은 배정할 수 없습니다"),
    CONSENT_NOT_FOUND(404, "SUR046", "해당 동의서가 존재하지 않습니다"),
    // SUR047 은 프론트 messages.ts 가 '동의서 미확인' 문구로 이미 쓰고 있어 건너뛴다.
    ANESTHESIA_RECORD_NOT_FOUND(404, "SUR048", "해당 마취기록이 존재하지 않습니다"),
    CHECKLIST_NOT_FOUND(404, "SUR049", "해당 체크리스트 항목이 존재하지 않습니다"),
    PLANNED_ITEM_NOT_FOUND(404, "SUR050", "해당 예정 자원이 존재하지 않습니다"),
    CHECKLIST_PREV_PHASE_INCOMPLETE(400, "SUR051", "이전 단계 체크리스트가 완료되지 않았습니다"),
    // 서비스가 직접 던지지 않고 GlobalExceptionHandler 가 안전망으로 쓰는 코드다.
    // 어느 자원인지는 로그에만 남기고 사용자에게는 이 코드만 내려간다(§15.1).
    RESOURCE_NOT_FOUND(404, "SUR052", "요청한 자원을 찾을 수 없습니다");

    private final int code;           // HTTP status
    private final String messageCode; // 프론트 messages.ts 매핑 키 (SURxxx)
    private final String message;     // 서버 기본 문구(로그·폴백용)
}
