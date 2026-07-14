package kr.co.seoulit.hisback.surgery.global.common;

/**
 * 공통 API 응답 포맷
 * <p>프론트-백엔드 개발표준가이드 §11.3: {@code {code, message, data}} 형식을 그대로 따른다.
 * code는 HTTP status 값을 그대로 담는다 (200/201 = 성공, 400/404/409/500 등 = 실패).</p>
 */
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(200, null, null);
    }

    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
