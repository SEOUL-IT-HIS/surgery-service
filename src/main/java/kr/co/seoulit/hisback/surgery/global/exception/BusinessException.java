package kr.co.seoulit.hisback.surgery.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 공통 예외
 *
 * <p>도메인 규칙 위반, 리소스 미존재 등 컨트롤러 밖에서 발생하는
 * 예외를 이 타입으로 던지면 GlobalExceptionHandler가 일괄 처리한다.</p>
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    /** 상태코드를 지정하지 않으면 400 Bad Request로 처리한다. */
    public BusinessException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(HttpStatus.NOT_FOUND, message);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(HttpStatus.BAD_REQUEST, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(HttpStatus.CONFLICT, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
