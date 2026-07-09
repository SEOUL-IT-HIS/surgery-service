package kr.co.seoulit.hisback.surgery.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * {@link WardServiceClient}의 REST 구현체.
 */
@Component
public class WardServiceClientImpl implements WardServiceClient {

    private final RestClient restClient;

    public WardServiceClientImpl(
            @Value("${service.ward.base-url:http://localhost:8392}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public void notifySurgeryScheduled(Long surgeryId, String patientMpiId, LocalDateTime scheduledDt) {
        restClient.post()
                .uri("/api/v1/ward/notifications/surgery")
                .body(Map.of(
                        "surgeryId", surgeryId,
                        "patientMpiId", patientMpiId,
                        "scheduledDt", scheduledDt.toString()))
                .retrieve()
                .toBodilessEntity();
    }
}
