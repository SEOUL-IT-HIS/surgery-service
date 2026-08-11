package kr.co.seoulit.hisback.surgery.common.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 목록 조회(페이징) 응답 DTO.
 * <p>Spring Data의 {@code Page<T>}를 그대로 컨트롤러 응답에 쓰면 pageable/sort/first/last 등
 * 불필요한 내부 메타데이터가 그대로 노출되어 가이드 §11.3이 요구하는 "단순 객체" data 형태와
 * 어긋난다. 그래서 필요한 필드만 골라 이 DTO로 감싸서 {@code ApiResponse<PageResponse<T>>}의
 * data 안에 넣는다.</p>
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
