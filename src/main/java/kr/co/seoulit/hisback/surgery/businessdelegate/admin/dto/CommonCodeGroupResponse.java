package kr.co.seoulit.hisback.surgery.businessdelegate.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * admin-service 의 공통코드 그룹 응답 한 건.
 *
 * <p>{@code GET /api/commonCodeGroup/list} 가 돌려주는 항목이다. admin 의
 * CommonCodeGroupEntity 와 필드명을 맞췄다.</p>
 *
 * <p>필요한 건 {@code groupCode → groupId} 변환뿐이다. 항목 조회 API 가 groupCode 가 아니라
 * groupId 를 받기 때문에, 어떤 조회든 그룹 목록을 먼저 훑어야 한다.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonCodeGroupResponse {

    /** UUID 36자. admin 이 @GeneratedValue(UUID) 로 채번한다. */
    private String groupId;

    /** SURGERY_STATUS_CD 처럼 사람이 읽는 그룹 식별자. 수술 코드에서 참조하는 값이다. */
    private String groupCode;

    private String groupName;

    /** 'Y'/'N'. 'N' 인 그룹은 캐시에 담지 않는다. */
    private String useYn;
}
