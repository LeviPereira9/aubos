package lp.boble.aubos.config.rateLimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.service.redis.RedisRateLimitService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor  implements HandlerInterceptor {

    private final RedisRateLimitService rateLimitService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

            if(rateLimit != null) {
                String clientKey = rateLimitService.getClientKey(request, request.getRequestURI());

                if(!rateLimitService.isAllowed(clientKey, rateLimit.requests(), rateLimit.time())){

                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{" +
                                    "\"error\": \"Rate limit exceeded\", " +
                                    "\"message\": \"Muitas requisições. Tente novamente em alguns minutos.\"" +
                            "}"
                    );
                    return false;
                }
            }
        }

        return true;
    }

}
