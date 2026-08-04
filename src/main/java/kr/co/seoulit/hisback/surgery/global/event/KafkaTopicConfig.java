package kr.co.seoulit.hisback.surgery.global.event;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka 토픽 선언
 *
 * <p>애플리케이션 기동 시 토픽이 없으면 자동 생성된다(KafkaAdmin). 브로커의
 * auto-create에 의존하면 파티션·복제 수가 브로커 기본값으로 잡혀버리므로,
 * 소유 서비스인 Surgery가 명시적으로 선언한다.</p>
 *
 * <p>로컬 단일 브로커 기준이라 replicas=1이다. 운영 브로커가 여러 대로 늘어나면
 * 복제본 수를 올려야 한다 — 그렇지 않으면 producer의 acks=all이 사실상 무의미해진다.</p>
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic surgeryCompletedTopic(
            @Value("${surgery.kafka.topic.surgery-completed}") String topicName) {
        return TopicBuilder.name(topicName).partitions(3).replicas(1).build();
    }
}
