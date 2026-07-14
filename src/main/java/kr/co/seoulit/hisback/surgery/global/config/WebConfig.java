package kr.co.seoulit.hisback.surgery.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 공통 웹 설정 (CORS 등)
 *
 * <p>MSA 구조상 프론트엔드/타 서비스 게이트웨이에서의 호출을 허용한다.
 * 운영 배포 시에는 allowedOrigins를 실제 도메인으로 제한해야 한다.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
