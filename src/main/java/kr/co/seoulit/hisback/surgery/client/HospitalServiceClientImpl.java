package kr.co.seoulit.hisback.surgery.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * {@link HospitalServiceClient}의 REST 구현체.
 */
@Component
public class HospitalServiceClientImpl implements HospitalServiceClient {

    private final RestClient restClient;

    public HospitalServiceClientImpl(
            @Value("${service.hospital.base-url:http://localhost:8391}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public EmployeeInfo getEmployee(String employeeId) {
        return restClient.get()
                .uri("/api/v1/employees/{employeeId}", employeeId)
                .retrieve()
                .body(EmployeeInfo.class);
    }
}
