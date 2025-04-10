package io.github.pangju666.framework.data.redis

import io.github.pangju666.framework.data.redis.bean.JavaRedisTemplate
import io.github.pangju666.framework.data.redis.bean.JsonRedisTemplate
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory

@SpringBootConfiguration
class BeanConfig {
	@Bean
	static JavaRedisTemplate javaRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new JavaRedisTemplate(connectionFactory)
	}

	@Bean
	static JsonRedisTemplate jsonRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new JsonRedisTemplate(connectionFactory)
	}
}
