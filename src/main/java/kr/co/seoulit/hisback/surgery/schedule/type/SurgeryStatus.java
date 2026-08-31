package kr.co.seoulit.hisback.surgery.schedule.type;

/**
 * SURGERY.status_cd (SURGERY_STATUS_CD) 코드값 상수.
 *
 * <p>코드 카탈로그의 소유·유효성 검증은 admin-service COMMON_CODE 소관(§21.4)이며,
 * 여기서는 수술 서비스가 <b>상태 전이 로직에서 다루는 값만</b> 상수로 참조한다.</p>
 *
 * <p><b>SURGERY_STATUS_CD 는 admin-service 에 등록돼 있다</b>(2026-08-25 확인 — 00~04 다섯 건).
 * 다만 이 상수는 서버가 전이 로직에서 비교하는 값이라 캐시를 거치지 않는다.</p>
 * <p>예전 주석은 미등록으로 적혀 있었다. 공통코드는 각
 * 서비스가 직접 작성하기로 정해졌으므로 등록도 수술이 한다. 등록 전까지는
 * 화면이 코드값을 그대로 보여준다.</p>
 */
public final class SurgeryStatus {

    private SurgeryStatus() {
    }

    /** 요청접수 — 진료·응급실이 요청했고 수술실이 아직 배정되지 않은 상태 */
    public static final String REQUESTED = "00";

    public static final String SCHEDULED = "01"; // 예약(배정 완료)
    public static final String IN_PROGRESS = "02"; // 진행중
    public static final String COMPLETED = "03"; // 완료
    public static final String CANCELLED = "04"; // 취소
}
