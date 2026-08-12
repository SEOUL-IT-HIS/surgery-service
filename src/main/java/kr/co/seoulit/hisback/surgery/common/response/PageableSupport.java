package kr.co.seoulit.hisback.surgery.common.response;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 목록 조회 파라미터(page/size/sort)를 {@link Pageable} 로 조립한다. (SL2-246)
 *
 * <h3>왜 필요한가</h3>
 * <p>{@code Sort.by(sort)} 에 {@code "createdAt,desc"} 를 그대로 넣으면 <b>문자열 전체를
 * 컬럼명 하나로</b> 본다. "createdAt,desc" 라는 속성이 없으니 500 이 난다. 프론트
 * {@code PageParams} 는 {@code "roomName,desc"} 형식을 쓰기로 되어 있어, 정렬 방향을
 * 보내는 순간 터진다.</p>
 *
 * <p>수술실·장비 목록도 같은 방식이라 같은 문제를 안고 있다 — 지금까지 프론트가 방향을
 * 보내지 않아 드러나지 않았을 뿐이다.</p>
 *
 * <h3>왜 Spring 기본 Pageable 인자를 안 쓰는가</h3>
 * <p>{@code @PageableDefault Pageable pageable} 로 받으면 Spring 이 알아서 파싱해 이 클래스가
 * 필요 없다. 다만 기존 컨트롤러들이 전부 page/size/sort 를 직접 받는 형태라, 한쪽만
 * 바꾸면 같은 코드베이스에 두 방식이 섞인다. 나중에 일괄 전환할 때 이 클래스를 지우면 된다.</p>
 */
public final class PageableSupport {

    private PageableSupport() {
    }

    /**
     * @param page 0-base 페이지 번호
     * @param size 페이지 크기
     * @param sort {@code "속성"} 또는 {@code "속성,asc|desc"}. 비어 있으면 정렬 없음.
     */
    public static Pageable of(int page, int size, String sort) {
        return of(page, size, sort, null);
    }

    /**
     * 정렬이 지정되지 않았을 때 쓸 기본 정렬을 함께 받는 형태.
     *
     * <p>기본 정렬을 주는 이유 — 정렬을 아예 지정하지 않으면 DB 가 돌려주는 순서에 맡기게
     * 되는데 그 순서는 보장되지 않는다. 페이지를 넘길 때 같은 행이 두 번 나오거나 빠질 수 있다.</p>
     */
    public static Pageable of(int page, int size, String sort, Sort defaultSort) {
        Sort resolved = parse(sort);
        if (resolved.isUnsorted() && defaultSort != null) {
            resolved = defaultSort;
        }
        return resolved.isUnsorted()
                ? PageRequest.of(page, size)
                : PageRequest.of(page, size, resolved);
    }

    /**
     * {@code "속성,desc"} → {@code Sort}.
     *
     * <p>방향이 없으면 오름차순이다(Spring Data 기본과 같다). 방향 문자열이 asc/desc 가
     * 아니면 오름차순으로 본다 — 오타 하나로 목록 조회 전체가 500 이 되는 것보다,
     * 순서가 기대와 다른 편이 낫다.</p>
     */
    private static Sort parse(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            return Sort.by(Sort.Direction.DESC, property);
        }
        return Sort.by(Sort.Direction.ASC, property);
    }
}
