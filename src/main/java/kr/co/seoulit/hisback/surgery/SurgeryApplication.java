package kr.co.seoulit.hisback.surgery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 수술관리 서비스 진입점 (surgery-service)
 *
 * <p>{@code @SpringBootApplication} 하나가 세 가지를 한다 —
 * 설정 클래스 선언(@Configuration), 자동 설정 활성화(@EnableAutoConfiguration),
 * 그리고 <b>이 클래스가 속한 패키지 아래를 훑어 빈을 등록</b>(@ComponentScan)한다.</p>
 *
 * <p>그래서 이 파일의 위치가 중요하다. {@code kr.co.seoulit.hisback.surgery} 에 있어야
 * 하위의 room·schedule·consent 같은 도메인 패키지가 모두 스캔 범위에 들어온다.
 * 더 깊은 곳으로 옮기면 바깥 패키지의 @RestController·@Service 가 등록되지 않아
 * 요청이 404 로 떨어진다.</p>
 *
 * <p>포트는 8383 이다(application.properties). 다른 서비스는 8080 을 쓰므로
 * 프론트 next.config.ts 의 rewrite 대상 포트를 맞출 때 주의한다.</p>
 */
// @EnableScheduling — 이게 없으면 @Scheduled 가 붙은 메서드를 스프링이 그냥 무시한다.
//                     오류도 경고도 없이 조용히 안 도는 것이라 빠뜨리면 원인을 찾기 어렵다.
//                     현재 대상은 CommonCodeCache 의 주기적 공통코드 갱신 하나다.
@EnableScheduling
@SpringBootApplication
public class SurgeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurgeryApplication.class, args);
    }

}
