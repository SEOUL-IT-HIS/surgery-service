package kr.co.seoulit.hisback.surgery.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 공통 웹 설정 (CORS)
 *
 * <p><b>2026-08-11 이전에는 애노테이션 없는 빈 클래스였다.</b> Javadoc 에만 "CORS 등"이라
 * 적혀 있어 설정이 된 줄 알기 쉬웠지만, {@code @Configuration} 이 없어 스프링이 빈으로
 * 등록조차 하지 않았다. 참조하는 곳도 없는 죽은 코드였다.</p>
 *
 * <p><b>지금 당장은 없어도 동작한다.</b> 프론트가 {@code next.config.ts} 의 rewrite 로
 * 프록시하기 때문이다. rewrite 는 브라우저가 아니라 Next 서버가 대신 호출하는 방식이라
 * 애초에 교차 출처가 아니다. 문제는 브라우저에서 8383 을 직접 부르는 순간 — Swagger UI 를
 * 다른 출처에서 열거나, 프록시를 거치지 않는 화면을 만들 때 막힌다.</p>
 *
 * <p>patient-service 는 같은 목적을 {@code CorsConfig} 라는 이름으로 구현했다. 이름만 다르고
 * 설정 키({@code app.cors.*})는 맞춰 뒀으므로, 나중에 팀이 한쪽으로 통일하기 쉽다.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 허용할 출처 패턴.
     *
     * <p>{@code allowedOrigins} 가 아니라 {@code allowedOriginPatterns} 를 쓰는 이유 —
     * 자격증명을 허용한 상태에서 {@code *} 를 쓰면 스프링이 거부한다. 패턴 방식은 실제 요청의
     * Origin 을 그대로 돌려주므로 둘을 함께 쓸 수 있다.</p>
     *
     * <p>팀원마다 각자 PC 에서 서버를 띄워 출처가 제각각이라 개발 단계에서는 열어 둔다.
     * 운영 전환 시에는 반드시 실제 도메인으로 좁혀야 한다.</p>
     */
    @Value("${app.cors.allowed-origin-patterns}")
    private String[] allowedOriginPatterns;

    @Value("${app.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    /** preflight(OPTIONS) 응답을 브라우저가 캐시할 초. 매 요청마다 왕복하지 않게 한다. */
    @Value("${app.cors.max-age}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}
