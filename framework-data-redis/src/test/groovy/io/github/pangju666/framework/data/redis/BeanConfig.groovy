package io.github.pangju666.framework.data.redis

import io.github.pangju666.framework.data.redis.core.JavaScanRedisTemplate
import io.github.pangju666.framework.data.redis.core.JsonScanRedisTemplate
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory

@SpringBootConfiguration
class BeanConfig {
	@Bean
	static JavaScanRedisTemplate javaRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new JavaScanRedisTemplate(connectionFactory)
	}

	@Bean
	static JsonScanRedisTemplate jsonRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new JsonScanRedisTemplate(connectionFactory)
	}
}
