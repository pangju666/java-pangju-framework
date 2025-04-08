package io.github.pangju666.framework.data.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTemplateUtilsTest {
	@Autowired
	StringRedisTemplate redisTemplate;
}
