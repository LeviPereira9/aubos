package lp.boble.aubos.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
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

       ObjectMapper objectMapper = new ObjectMapper()
               .registerModule(new ParameterNamesModule())
               .registerModule(new Jdk8Module())
               .registerModule(new JavaTimeModule())
               .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

       PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
               .allowIfSubType(Object.class)
               .build();

       objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

       Jackson2JsonRedisSerializer<Object> serializer =
               new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);


       //Configuração padrão
       RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
               .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                       serializer
               ))
               .entryTtl(Duration.ofMinutes(30));

       // Configuração especificas
       Map<String, RedisCacheConfiguration> customConfigs = Map.of(
               "bookSearch", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)),
               "userSearch", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)),
               "userAutocomplete", defaultCacheConfig.entryTtl(Duration.ofMinutes(5))
       );

       return RedisCacheManager.builder(connectionFactory)
               .cacheDefaults(defaultCacheConfig)
               .withInitialCacheConfigurations(customConfigs)
               .build();
   }
}
