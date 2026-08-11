package kr.co.seoulit.hisback.surgery.common.exception;

import java.util.NoSuchElementException;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    /**
     * 있는 경로를 잘못된 방식으로 부른 경우 (예: DELETE 전용 경로를 GET 으로 호출)
     *
     * <p>이 핸들러가 없으면 아래 {@code Exception.class} 가 잡아 <b>500</b> 을 내보낸다.
     * 프론트 입장에서는 "서버가 터졌다"와 "주소를 잘못 불렀다"가 구분되지 않아,
     * 재시도해야 할 상황인지 코드를 고쳐야 할 상황인지 판단할 수 없다.</p>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException e) {
        log.warn("허용되지 않는 요청 방식: {} (허용: {})", e.getMethod(), e.getSupportedHttpMethods());
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getCode())
                .body(ApiResponse.error(
                        ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                        ErrorCode.METHOD_NOT_ALLOWED.getMessageCode()));
    }

    /**
     * 아예 존재하지 않는 경로를 부른 경우.
     *
     * <p>두 예외를 함께 받는 이유 — 스프링 버전과 설정에 따라 둘 중 하나가 올라온다.
     * {@code NoResourceFoundException} 은 정적 자원 처리기가, {@code NoHandlerFoundException} 은
     * DispatcherServlet 이 던진다. 어느 쪽이든 사용자에게는 같은 상황이다.</p>
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNoHandler(Exception e) {
        log.warn("존재하지 않는 경로: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getCode())
                .body(ApiResponse.error(
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessageCode()));
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
