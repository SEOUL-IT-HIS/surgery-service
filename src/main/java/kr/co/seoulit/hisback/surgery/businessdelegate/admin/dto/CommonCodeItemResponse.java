package kr.co.seoulit.hisback.surgery.businessdelegate.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * admin-service 의 공통코드 항목 응답 한 건.
 *
 * <p>{@code GET /api/commonCodeItem/list?groupId={groupId}} 가 돌려주는 항목이다.</p>
 *
 * <p>{@code codeName} 을 받아두지만 지금은 쓰지 않는다. 캐시가 하는 일이 "이 값이 유효한가"
 * 판정이라 코드값만 있으면 되고, 화면에 보일 이름은 프론트가 admin 에서 직접 가져간다(§2.1).
 * 백엔드가 만드는 문서·리포트에 이름이 필요해지면 그때 캐시 구조를 넓힌다.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonCodeItemResponse {

    private String codeId;
    private String groupId;

    /** "01" 처럼 실제 저장·전송되는 값. 캐시가 담는 건 이것이다. */
    private String codeValue;

    /** "예약" 처럼 화면에 보이는 이름. 현재 미사용 — 위 설명 참고. */
    private String codeName;

    /** 표시 순서. admin 이 IH2-20 에서 정렬 기준으로 채택했다. */
    private Integer sortOrder;

    /** 'Y'/'N'. 'N' 인 항목은 캐시에 담지 않는다 — 폐기된 코드로 저장되면 안 된다. */
    private String useYn;
}
