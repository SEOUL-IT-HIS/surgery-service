package kr.co.seoulit.hisback.surgery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * created_at/updated_at 자동 기록을 위해 JPA Auditing을 활성화한다.
 * (개발표준가이드 §14.1: 모든 테이블은 created_at/updated_at 포함)
 */
@EnableJpaAuditing
@SpringBootApplication
public class SurgeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurgeryApplication.class, args);
    }

}
