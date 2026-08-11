package kr.co.seoulit.hisback.surgery.businessdelegate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 타 서비스 응답 봉투(envelope).
 *
 * <p>수술의 {@code common/response/ApiResponse} 와 모양이 같지만 <b>따로 둔다</b>.
 * 저쪽은 우리가 <i>내보내는</i> 계약이고 이건 남이 <i>보내온</i> 형식이라, 한 클래스로 겸하면
 * admin 이 응답 모양을 바꿨을 때 우리 응답 계약까지 흔들린다.</p>
 *
 * <p>{@code code}·{@code message} 는 역직렬화 대상으로만 두고 쓰지 않는다. admin 이 필드를
 * 더 붙여도 깨지지 않도록 Jackson 기본 설정(알 수 없는 필드 무시)에 기댄다.</p>
 *
 * @param <T> data 에 담기는 실제 자료형
 */
@Getter
@Setter
@NoArgsConstructor
public class ExternalApiResponse<T> {

    private int code;
    private String message;
    private T data;
}
