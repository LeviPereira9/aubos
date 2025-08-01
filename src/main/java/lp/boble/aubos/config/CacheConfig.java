package lp.boble.aubos.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.PathMatcher;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

   @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
       //Configuração padrão
       RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
               .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                       new GenericJackson2JsonRedisSerializer()//Serializa para JSON
               ))
               .entryTtl(Duration.ofMinutes(30));

       // Configuração especificas
       Map<String, RedisCacheConfiguration> customConfigs = Map.of(
               "bookSearch", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)),
               "userSearch", defaultCacheConfig.entryTtl(Duration.ofMinutes(5))
       );

       return RedisCacheManager.builder(connectionFactory)
               .cacheDefaults(defaultCacheConfig)
               .withInitialCacheConfigurations(customConfigs)
               .build();
   }
}
