package kr.co.seoulit.hisback.surgery.schedule.type;

/**
 * SURGERY.status_cd (SURGERY_STATUS_CD) 코드값 상수.
 *
 * <p>코드 카탈로그의 소유·유효성 검증은 admin-service COMMON_CODE 소관(§21.4)이며,
 * 여기서는 수술 서비스가 <b>상태 전이 로직에서 다루는 값만</b> 상수로 참조한다.</p>
 *
 * <p><b>REQUESTED("00") 는 admin-service 공통코드에 아직 등록되지 않았다.</b>
 * 진료·응급실이 올린 요청을 배정 전 단계로 구분하기 위해 프론트와 합의한 값이며,
 * admin 이 다른 코드값을 부여하면 여기만 바꾸면 된다.</p>
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
