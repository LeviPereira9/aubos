package lp.boble.aubos.service.redis;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisRateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String clientKey, int maxRequests, int timeWindowMinutes){
        String redisKey = "rateLimit:" + clientKey;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if(count != null && count == 1){
            redisTemplate.expire(redisKey, timeWindowMinutes, TimeUnit.MINUTES);
        }

        return count != null && count <= maxRequests;
    }

    public String getClientKey(HttpServletRequest request, String endpoint){
        String ip = this.getClientIp(request);

        return ip + ":" + endpoint;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");

        if(xfHeader != null){
            return xfHeader.split(",")[0];
        }

        return request.getRemoteAddr();
    }
}
