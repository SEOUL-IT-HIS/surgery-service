package kr.co.seoulit.hisback.surgery.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * 공유 로그인 세션의 직렬화 설정.
 *
 * <p>세션을 Redis 에 두는 것만으로는 부족하다. 기본값인 JDK 직렬화로 저장하면 바이트 덩어리가
 * 들어가서, 다른 서비스가 같은 값을 읽어도 자기 클래스로 되살리지 못한다. 그래서 JSON 으로
 * 저장하고, 어떤 클래스였는지를 {@code "@class"} 필드에 함께 적는다.</p>
 *
 * <p><b>빈 이름 {@code springSessionDefaultRedisSerializer} 는 바꾸면 안 된다.</b>
 * Spring Session 이 타입이 아니라 <i>이름</i>으로 이 빈을 찾는다. 이름이 다르면 못 찾고
 * 조용히 JDK 직렬화로 되돌아간다. 오류가 없어서 알아차리기 어렵다.</p>
 *
 * <p>허용 패키지를 좁히는 이유 — {@code "@class"} 에 적힌 이름을 그대로 믿고 객체를 만들면,
 * Redis 에 이상한 값을 넣을 수 있는 사람이 원하는 클래스를 생성시킬 수 있다.
 * {@code java.lang.} 까지 열어두면 {@code ProcessBuilder} 같은 것이 들어온다.
 * 우리가 실제로 주고받는 것은 {@code SessionUser} 하나뿐이므로 그 패키지만 연다.</p>
 */
@Configuration
public class RedisSessionConfig {

    /** "@class" 에 적힌 클래스를 되살릴 때 허용할 범위 (SessionUser 가 이 아래 있다). */
    private static final String ALLOWED_PACKAGE = "kr.co.seoulit.his.";

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        PolymorphicTypeValidator allowedTypes = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ALLOWED_PACKAGE)
                .build();

        return GenericJacksonJsonRedisSerializer.builder()
                .customize(builder -> builder.activateDefaultTyping(
                        allowedTypes,
                        DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY))
                .build();
    }
}
