package kr.co.seoulit.his.common.session;

import lombok.Data;

/**
 * 세션에 담는 로그인 사용자.
 *
 * <p>모든 MSA 서비스가 이 파일을 그대로 복사해서 쓴다. <b>패키지 경로까지 똑같아야 한다.</b>
 * Redis 에 저장되는 JSON 의 {@code "@class"} 에 이 경로가 그대로 적히고, 읽는 쪽은 그
 * 문자열로 클래스를 되살리기 때문이다. 경로가 다르면 수술만 세션을 못 읽는다.</p>
 *
 * <p><b>수술만 앱 패키지가 {@code kr.co.seoulit.hisback.surgery} 다</b>(다른 팀은 his).
 * 그래도 이 파일만은 {@code kr.co.seoulit.his.common.session} 에 둔다. 빈이 아니라
 * 컴포넌트 스캔 범위 밖이어도 상관없다 — 세션에서 꺼낼 때 클래스로 찾을 뿐이다.</p>
 *
 * <p><b>필드 이름이 팀 간 약속이다.</b> 바꾸면 다른 서비스가 깨진다.</p>
 *
 * <p>역할·메뉴를 {@code List} 가 아니라 쉼표 문자열로 담는 이유 — {@code "@class"} 표시가
 * 컬렉션에도 붙어서 {@code ["java.util.ArrayList",["01"]]} 처럼 한 겹 감싸진다.
 * 서비스마다 역직렬화 설정이 조금씩 달라 그 모양에서 어긋나기 쉽다.</p>
 */
@Data
public class SessionUser {

    /** ACCOUNT.ACCOUNT_ID */
    private String accountId;

    /** 계정 상태 (01 = 정상) */
    private String accountStatus;

    /** EMPLOYEE.EMP_ID — "작성자" 같은 값으로 저장할 때 쓰는 키 */
    private String empId;

    /** 사번 (예: E202608001) */
    private String empNo;

    /** 직원 이름 */
    private String empName;

    /** 부서 공통코드 (DEPT_CD) */
    private String deptCode;

    /** 로그인 아이디 */
    private String loginId;

    /** 역할 코드들. 쉼표로 이어 붙인다 (예: "01" 또는 "01,02"). 없으면 "" */
    private String roleCodes;

    /** 볼 수 있는 메뉴 코드들. 쉼표로 이어 붙인다. 없으면 "" */
    private String menuCodes;
}
