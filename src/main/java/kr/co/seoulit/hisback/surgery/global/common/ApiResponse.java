package kr.co.seoulit.hisback.surgery.global.common;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 포맷
 * <p>모든 REST 응답을 {success, message, data, timestamp} 형태로 통일한다.</p>
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /** 성공 응답 (기본 메시지) */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    /** 성공 응답 (메시지 지정) */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** 실패 응답 */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
