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

    /**
     * 추적용 상세 문구. 못 찾은 식별자처럼 <b>로그에만</b> 남길 값을 담는다.
     *
     * <p>응답에는 절대 실리지 않는다(§15.1). 식별자가 그대로 노출되면 사용자에게
     * 의미도 없고, 다른 사람의 데이터 존재 여부를 알려주는 통로가 되기도 한다.
     * 없으면 null 이며, 이때 로그에는 ErrorCode 기본 문구만 남는다.</p>
     */
    private final String detail;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        // 예외 메시지는 스택트레이스에 찍히므로 상세가 있으면 그쪽을 쓴다
        super(detail != null ? errorCode.getMessage() + ": " + detail : errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
