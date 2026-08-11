package kr.co.seoulit.hisback.surgery.global.exception;

import java.util.NoSuchElementException;
import kr.co.seoulit.hisback.surgery.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 (가이드 §11.5: 서버 예외는 공통 Exception Handler로 일괄 처리)
 * <p>업무 예외는 응답 message 에 프론트 messages.ts 코드(SURxxx)를 실어 보낸다(§15.2).
 * 상세 한글 문구/스택트레이스는 사용자에게 노출하지 않고 로그로만 남긴다(§15.1).</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 대상 리소스를 찾지 못한 경우의 안전망.
     *
     * <p>업무 코드는 모두 BusinessException 을 던지므로 여기까지 오면 안 된다. 라이브러리
     * 내부나 미처 손대지 못한 경로에서 올라온 것이라, 예외 문구를 그대로 내보내는 대신
     * 로그로만 남기고 사용자에게는 공통 코드(SUR052)를 준다(§15.1).</p>
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException e) {
        log.warn("자원 없음(BusinessException 미적용 경로): {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getCode())
                .body(ApiResponse.error(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessageCode()));
    }

    /** 요청 값이 잘못된 경우의 안전망. 위와 같은 이유로 문구 대신 코드(SUR038)를 내려준다. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException e) {
        log.warn("잘못된 요청(BusinessException 미적용 경로): {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getCode())
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST.getCode(),
                        ErrorCode.INVALID_REQUEST.getMessageCode()));
    }

    /** 업무 예외 — 응답 message 에는 SUR 코드를, 상세(못 찾은 식별자 등)는 로그로만 남긴다. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(
                "업무 예외 [{}] {}{}",
                errorCode.getMessageCode(),
                errorCode.getMessage(),
                e.getDetail() != null ? " (" + e.getDetail() + ")" : "");
        return ResponseEntity.status(errorCode.getCode())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessageCode()));
    }

    /** {@code @Valid} 바인딩 실패 — 어느 필드가 틀렸는지는 로그로만 남기고 공통 코드만 내려준다(§15.1). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        log.warn("입력값 검증 실패: {}", e.getBindingResult().getFieldErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_REQUEST.getCode(),
                        ErrorCode.INVALID_REQUEST.getMessageCode()));
    }

    /** 그 외 예상치 못한 서버 오류 — 원본은 로그로만, 사용자에겐 공통 코드(SUR040)를 내려준다. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("예상치 못한 서버 오류", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessageCode()));
    }
}
