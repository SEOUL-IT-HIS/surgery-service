package kr.co.seoulit.hisback.surgery.common.exception;

import java.util.NoSuchElementException;
import kr.co.seoulit.hisback.surgery.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    /**
     * 요청 파라미터의 타입이 맞지 않는 경우 (예: {@code ?page=abc}, {@code ?date=abc})
     *
     * <p>스프링이 문자열을 int·LocalDate 로 바꾸다 실패한 것이므로 <b>보낸 쪽 잘못</b>이다.
     * 아래 {@code Exception.class} 로 떨어지면 500 이 되어, 서버 장애 알림이 클라이언트
     * 실수로 오염된다.</p>
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        log.warn("파라미터 타입 불일치: {}={} (기대 타입 {})",
                e.getName(), e.getValue(), e.getRequiredType());
        return badRequest();
    }

    /** 필수 쿼리 파라미터가 빠진 경우 (예: {@code /consents} 에 patientId 누락) */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException e) {
        log.warn("필수 파라미터 누락: {} ({})", e.getParameterName(), e.getParameterType());
        return badRequest();
    }

    /**
     * 본문 JSON 을 읽지 못한 경우 (깨진 JSON, 숫자 자리에 문자열 등)
     *
     * <p>patient-service 도 같은 예외를 400 으로 처리한다. 형식이 깨진 요청은 서버가
     * 고칠 수 있는 게 없으므로 보낸 쪽에 알려주는 것이 맞다.</p>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(
            HttpMessageNotReadableException e) {
        // 본문 원문에 환자 정보가 섞일 수 있어 예외 메시지만 남기고 본문은 로그에도 남기지 않는다
        log.warn("요청 본문을 읽을 수 없습니다: {}", e.getMostSpecificCause().getMessage());
        return badRequest();
    }

    /** Content-Type 이 없거나 JSON 이 아닌 경우 — 400 이 아니라 415 가 맞다. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException e) {
        log.warn("지원하지 않는 Content-Type: {} (지원: {})",
                e.getContentType(), e.getSupportedMediaTypes());
        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode())
                .body(ApiResponse.error(
                        ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                        ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessageCode()));
    }

    /** 400(SUR038) 응답이 여러 곳에서 같아 한곳으로 모았다. */
    private ResponseEntity<ApiResponse<Void>> badRequest() {
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getCode())
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST.getCode(),
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
