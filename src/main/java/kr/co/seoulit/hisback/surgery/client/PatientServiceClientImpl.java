package kr.co.seoulit.hisback.surgery.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * {@link PatientServiceClient}의 REST 구현체.
 * <p>환자관리 서비스의 base-url은 {@code service.patient.base-url} 프로퍼티로 주입한다(미설정 시 기본값 사용).</p>
 */
@Component
public class PatientServiceClientImpl implements PatientServiceClient {

    private final RestClient restClient;

    public PatientServiceClientImpl(
            @Value("${service.patient.base-url:http://localhost:8390}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public PatientInfo getPatient(String patientMpiId) {
        return restClient.get()
                .uri("/api/v1/patients/{mpiId}", patientMpiId)
                .retrieve()
                .body(PatientInfo.class);
    }
}
