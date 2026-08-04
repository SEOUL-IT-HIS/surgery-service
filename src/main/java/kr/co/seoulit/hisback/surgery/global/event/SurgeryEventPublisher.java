package kr.co.seoulit.hisback.surgery.global.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 수술 도메인 이벤트 발행자 (가이드 §21.3 서비스 간 통신 — Event 방식)
 *
 * <p>이벤트 발행 실패가 수술 업무 자체를 막으면 안 되므로 예외를 밖으로 던지지 않는다.
 * 수술 완료 처리(DB 저장)는 이미 끝난 상태이고, 청구 연계는 그 후속 통보이기 때문이다.
 * 대신 실패를 로그로 남겨 재처리 대상으로 남긴다.</p>
 */
@Component
public class SurgeryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SurgeryEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /** 토픽명은 수납팀과 합의된 값이라 코드에 하드코딩하지 않고 설정으로 뺀다. */
    private final String surgeryCompletedTopic;

    public SurgeryEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${surgery.kafka.topic.surgery-completed}") String surgeryCompletedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.surgeryCompletedTopic = surgeryCompletedTopic;
    }

    /**
     * 수술 완료 사실을 발행한다.
     *
     * <p>메시지 키를 surgeryId로 두는 이유: Kafka는 같은 키를 같은 파티션으로 보내므로,
     * 동일 수술에 대한 이벤트들의 순서가 보장된다. 키가 없으면 라운드로빈으로 흩어져
     * 수신 측에서 순서가 뒤바뀔 수 있다.</p>
     */
    public void publishSurgeryCompleted(SurgeryCompletedEvent event) {
        try {
            kafkaTemplate.send(surgeryCompletedTopic, event.getSurgeryId(), event);
        } catch (Exception e) {
            // 사용자 화면에는 노출하지 않는다(§15.1 시스템 메시지/사용자 메시지 분리).
            // 수술 완료 자체는 성공했으므로 예외를 다시 던지지 않는다.
            log.error("[surgery] 수술완료 이벤트 발행 실패 surgeryId={}", event.getSurgeryId(), e);
        }
    }
}
