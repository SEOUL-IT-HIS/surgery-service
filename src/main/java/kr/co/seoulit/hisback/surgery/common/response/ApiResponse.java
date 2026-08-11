package kr.co.seoulit.hisback.surgery.common.response;

import lombok.Getter;

/**
 * 공통 API 응답 포맷 (가이드 §11.3)
 * <p>{@code { "code": 200, "message": "SUCCESS", "data": {} }} 형태로 통일한다.
 * 프론트 ApiResponse&lt;T&gt; 타입(hisfrontend/src/features/surgery/types.ts)과 필드가
 * 1:1로 대응된다.</p>
 */
@Getter
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 200 SUCCESS + data */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", data);
    }

    /** 임의 성공 코드(예: 201 생성) + data */
    public static <T> ApiResponse<T> success(int code, T data) {
        return new ApiResponse<>(code, "SUCCESS", data);
    }

    /** 코드/메시지를 직접 지정 (data 없이 응답할 때 등) */
    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /** 실패 응답. message는 15장 메시지 코드 체계(SUR001 등) 또는 완성된 문구를 넣는다. */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
