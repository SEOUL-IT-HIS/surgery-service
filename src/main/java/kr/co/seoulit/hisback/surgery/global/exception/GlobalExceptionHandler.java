package kr.co.seoulit.hisback.surgery.global.exception;

import java.util.NoSuchElementException;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 (가이드 §11.5: 서버 예외는 공통 Exception Handler로 일괄 처리)
 * <p>시스템 메시지(스택트레이스 등)는 사용자 화면에 노출하지 않는다 (§15.1).</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 대상 리소스를 찾지 못한 경우 (예: findById().orElseThrow()) */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    /** 요청 값 검증 실패 등 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    /** 그 외 예상치 못한 서버 오류 — 원본 예외 메시지는 로그로만 남기고 사용자에게는 안전한 문구만 내려준다. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.error(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
