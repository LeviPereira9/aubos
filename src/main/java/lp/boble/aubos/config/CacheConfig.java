package lp.boble.aubos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.Warning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class CacheConfig {


    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {

        try {
            connectionFactory.getConnection().ping();
        } catch (Exception e) {
            log.error("Redis não disponível.");
        }

        // Mapeando o cache
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Serialização + TTL de 1 Hora.
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(mapper)))
                .entryTtl(Duration.ofHours(1));

        // TTL customizados
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("bookSearch", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("userSearch", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
