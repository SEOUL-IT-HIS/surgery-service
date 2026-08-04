package kr.co.seoulit.hisback.surgery.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 공통 예외
 * <p>{@link ErrorCode} 를 그대로 들고 다녀서 GlobalExceptionHandler 가 HTTP 상태코드와
 * 프론트 메시지코드(SURxxx)를 함께 꺼내 쓸 수 있게 한다(§11.5/§15.2).</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
