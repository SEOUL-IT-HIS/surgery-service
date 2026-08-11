package kr.co.seoulit.hisback.surgery.global.config;

import java.time.Duration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 타 서비스 REST 호출용 RestTemplate.
 *
 * <p>타임아웃을 반드시 지정한다. 기본값은 <b>무제한</b>이라, admin 이 응답을 안 주고 붙들고
 * 있으면 호출 스레드가 영영 묶인다. 공통코드 갱신은 백그라운드라 당장 사용자에게 보이진
 * 않지만, 스케줄러 스레드가 막히면 다음 갱신도 밀린다.</p>
 *
 * <p>연결 3초·응답 5초는 같은 LAN 안의 서비스를 부르는 기준이다. 이 시간을 넘기면 상대가
 * 느린 게 아니라 죽었다고 보는 편이 맞다.</p>
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }
}
