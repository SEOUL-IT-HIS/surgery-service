package kr.co.seoulit.hisback.surgery.schedule.type;

/**
 * 상태변경 이력이 어느 코드의 변화인지 구분하는 값 (SURGERY_STATUS_HISTORY.status_type)
 *
 * <p>Surgery 에는 성격이 다른 코드가 둘 있다. 한 테이블에 이력을 모으기로 했으므로
 * 어느 쪽이 바뀐 것인지 구분할 값이 필요하다.</p>
 * <ul>
 *   <li>{@link #STATUS} — statusCd. 요청접수→예약→진행중→완료/취소 같은 <b>큰 상태 전이</b></li>
 *   <li>{@link #PROGRESS} — progressCd. 수술 안에서의 <b>세부 진행단계</b></li>
 * </ul>
 *
 * <p><b>공통코드로 등록하지 않는 이유</b> — 이 값은 화면에서 고르는 것이 아니라 서버가
 * 이력을 남길 때 스스로 정하는 내부 구분자다. 사용자에게 보일 일이 없으므로 자바 상수로
 * 관리한다(2026-08-10 팀 합의: 화면에 표시하지 않고 서버 판단에만 쓰는 코드는 상수로 둔다).</p>
 */
public final class StatusChangeType {

    private StatusChangeType() {
    }

    /** 수술 상태(statusCd) 변경 */
    public static final String STATUS = "STATUS";

    /** 수술 진행단계(progressCd) 변경 */
    public static final String PROGRESS = "PROGRESS";
}
