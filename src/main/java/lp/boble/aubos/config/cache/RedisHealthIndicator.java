package lp.boble.aubos.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try{
            redisConnectionFactory.getConnection().ping();
            return Health.up().withDetail("message", "Redis está OK").build();
        } catch (Exception e) {
            return Health.down().withDetail("message", "Redis está fora do ar").withException(e).build();
        }
    }
}
