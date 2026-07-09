package kr.co.seoulit.hisback.surgery.client;

/**
 * 환자관리(PAT) 서비스 연동 클라이언트
 * <p>수술 대상 환자의 마스터 식별정보(MPI)를 조회한다. (IR-INT-004 / API-PAT-001)</p>
 */
public interface PatientServiceClient {

    /**
     * MPI ID로 환자 기본정보를 조회한다.
     *
     * @param patientMpiId 환자 마스터 식별자
     * @return 환자 기본정보 (조회 실패 시 null 또는 예외)
     */
    PatientInfo getPatient(String patientMpiId);

    /** 환자관리 서비스가 반환하는 최소 환자정보 */
    record PatientInfo(String patientMpiId, String name, String birthDate) {
    }
}
