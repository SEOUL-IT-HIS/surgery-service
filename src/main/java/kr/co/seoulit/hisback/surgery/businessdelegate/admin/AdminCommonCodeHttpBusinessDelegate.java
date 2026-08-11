package kr.co.seoulit.hisback.surgery.businessdelegate.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kr.co.seoulit.hisback.surgery.businessdelegate.admin.dto.CommonCodeGroupResponse;
import kr.co.seoulit.hisback.surgery.businessdelegate.admin.dto.CommonCodeItemResponse;
import kr.co.seoulit.hisback.surgery.businessdelegate.dto.ExternalApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * {@link AdminCommonCodeBusinessDelegate} 의 RestTemplate 구현체.
 *
 * <p><b>경로는 명세서가 아니라 admin 에 실제로 구현돼 프론트가 쓰고 있는 API 에 맞췄다.</b>
 * 프론트 {@code features/commonCode/api/*.ts} 와 같은 경로·같은 2단계 흐름이다.
 * admin 이 나중에 명세서 경로로 옮기면 이 클래스만 고치면 된다.</p>
 *
 * <p>2단계인 이유 — 항목 조회 API 가 groupCode 가 아니라 <b>groupId(UUID)</b> 를 받는다.
 * 그래서 어떤 조회든 그룹 목록을 먼저 훑어 groupCode → groupId 로 바꿔야 한다.</p>
 */
@Slf4j
@Component
public class AdminCommonCodeHttpBusinessDelegate implements AdminCommonCodeBusinessDelegate {

    private static final String CODE_GROUP_LIST_PATH = "/api/commonCodeGroup/list";
    private static final String CODE_ITEM_LIST_PATH = "/api/commonCodeItem/list?groupId={groupId}";

    private static final String USE_YN_Y = "Y";

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AdminCommonCodeHttpBusinessDelegate(
            RestTemplate restTemplate,
            @Value("${app.admin-service.host}") String host,
            @Value("${app.admin-service.port}") int port) {
        this.restTemplate = restTemplate;
        this.baseUrl = "http://" + host + ":" + port;
    }

    /**
     * 전체 공통코드를 그룹별로 읽어온다.
     *
     * <p><b>그룹 1회 + 그룹당 1회 = N+1 호출이다.</b> admin 에 "전부 한 번에" 주는 API 가 없어
     * 프론트와 같은 방식으로 돈다. 요청 처리 경로가 아니라 10분 주기 백그라운드 갱신에서만
     * 실행되므로 감수한다. 벌크 API 가 생기면 이 메서드만 한 번의 호출로 바꾸면 된다.</p>
     */
    @Override
    public Map<String, List<String>> getAllCodeValues() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (CommonCodeGroupResponse group : findUsableGroups()) {
            result.put(group.getGroupCode(), findUsableCodeValues(group.getGroupId()));
        }
        return result;
    }

    /** 사용중이고 groupCode·groupId 가 온전한 그룹만 남긴다. */
    private List<CommonCodeGroupResponse> findUsableGroups() {
        ResponseEntity<ExternalApiResponse<List<CommonCodeGroupResponse>>> response =
                restTemplate.exchange(
                        baseUrl + CODE_GROUP_LIST_PATH,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});

        ExternalApiResponse<List<CommonCodeGroupResponse>> body = response.getBody();
        List<CommonCodeGroupResponse> groups = (body == null) ? null : body.getData();
        if (groups == null) {
            log.warn("공통코드 그룹 목록 응답 본문이 비어 있습니다.");
            return List.of();
        }

        return groups.stream()
                .filter(group -> USE_YN_Y.equals(group.getUseYn()))
                .filter(group -> group.getGroupCode() != null && group.getGroupId() != null)
                .toList();
    }

    /**
     * 한 그룹의 사용중 코드값 목록.
     *
     * <p>404 를 여기서 삼키는 이유 — 그룹 목록을 읽은 뒤 항목을 읽기까지 사이에 그룹이
     * 지워질 수 있다. 그 한 그룹 때문에 나머지 갱신까지 통째로 실패하면 손해가 크다.</p>
     */
    private List<String> findUsableCodeValues(String groupId) {
        try {
            ResponseEntity<ExternalApiResponse<List<CommonCodeItemResponse>>> response =
                    restTemplate.exchange(
                            baseUrl + CODE_ITEM_LIST_PATH,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {},
                            groupId);

            ExternalApiResponse<List<CommonCodeItemResponse>> body = response.getBody();
            List<CommonCodeItemResponse> items = (body == null) ? null : body.getData();
            if (items == null) {
                log.warn("공통코드 항목 응답 본문이 비어 있습니다. groupId={}", groupId);
                return List.of();
            }

            return items.stream()
                    .filter(item -> USE_YN_Y.equals(item.getUseYn()))
                    .map(CommonCodeItemResponse::getCodeValue)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("존재하지 않는 공통코드 그룹입니다. groupId={}", groupId);
            return List.of();
        }
    }
}
