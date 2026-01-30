package depth.finvibe.gamification.config;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 테스트 환경용 Redis 설정
 * 실제 Redis 서버 연결 없이 Mock 객체를 사용
 */
@TestConfiguration
public class TestRedissonConfig {

  @Bean
  @Primary
  public RedissonClient redissonClient() {
    RedissonClient redissonClient = mock(RedissonClient.class);
    RLock lock = mock(RLock.class);

    when(redissonClient.getLock(anyString())).thenReturn(lock);
    try {
      when(lock.tryLock(any(Long.class), any(Long.class), any())).thenReturn(true);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
    when(lock.isHeldByCurrentThread()).thenReturn(true);

    return redissonClient;
  }

  @Bean
  @Primary
  public RedisConnectionFactory redisConnectionFactory() {
    return mock(RedisConnectionFactory.class);
  }

  @Bean
  @Primary
  public StringRedisTemplate stringRedisTemplate() {
    StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
    when(redisTemplate.execute(any(RedisScript.class), any(List.class), any())).thenAnswer(invocation -> {
      RedisScript<?> script = invocation.getArgument(0);
      if (List.class.equals(script.getResultType())) {
        return List.of(1L, 0L);
      }
      return 1L;
    });

    return redisTemplate;
  }
}
