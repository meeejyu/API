package com.jwt.config.cache;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

// 캐시를 사용하기 위한 Key와 디폴트 만료 시간을 설정한 클래스
@Configuration
@RequiredArgsConstructor
@EnableCaching // 이 어노테이션을 사용하면 RedisConnectionFactory를 생성해줌
public class CacheConfig {
    
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // 널값 금지(캐싱시 unless("#result == null") 필수)
                .entryTtl(Duration.ofSeconds(CacheKey.DEFAULT_EXPIRE_SEC)) // 캐쉬 유지 시간
                .computePrefixWith(CacheKeyPrefix.simple()) // name::key처럼 key 앞에"::"를 삽입(redis-cli에서 get "name::key" 로 조회)
                .serializeKeysWith( // key 직렬화에는 StringRedisSerializer 등록
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())) 
                .serializeValuesWith( // value 직렬화에는 GenericJackson2JsonRedisSerializer 등록
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        /* 
                StringRedisSerializer: binary 데이터로 저장되기 때문에 이를 String 으로 변환시켜주며(반대로도 가능) UTF-8 인코딩 방식을 사용한다.
                GenericJackson2JsonRedisSerializer: 객체를 json타입으로 직렬화/역직렬화를 수행한다.
                serializeKeysWith : 캐시 Key를 직렬화-역직렬화 하는데 사용하는 Pair를 지정 -> String으로 지정
                serializeValuesWith - 캐시 Value를 직렬화-역직렬화 하는데 사용하는 Pair를 지정 -> Value는 다양한 자료구조가 올 수 있으므로 JsonSerializer 사용
         */

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(configuration)
                .build();

    }
}
