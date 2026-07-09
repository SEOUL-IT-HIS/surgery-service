package kr.co.seoulit.hisback.surgery.global.exception;

/**
 * 비즈니스 로직 공통 예외
 * <p>도메인 규칙 위반(중복, 상태 위반, 미존재 등) 시 발생시키며,
 * {@link GlobalExceptionHandler}에서 400 응답으로 변환한다.</p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
