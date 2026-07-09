package kr.co.seoulit.hisback.surgery.client;

/**
 * 병원관리(HOS) 서비스 연동 클라이언트 (직원정보 등)
 * <p>집도의/마취의 등 의료진 정보를 조회한다. (IR-INT-006)</p>
 */
public interface HospitalServiceClient {

    /**
     * 직원 사번으로 직원 정보를 조회한다.
     *
     * @param employeeId 직원 사번
     * @return 직원 정보 (조회 실패 시 null 또는 예외)
     */
    EmployeeInfo getEmployee(String employeeId);

    /** 병원관리 서비스가 반환하는 최소 직원정보 */
    record EmployeeInfo(String employeeId, String name, String deptCode, String roleType) {
    }
}
