package io.github.pangju666.framework.data.redis


import io.github.pangju666.framework.data.redis.bean.JsonRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class JsonRedisOperationsUtilsSpec extends Specification {
	@Autowired
	JsonRedisTemplate jsonRedisTemplate
}
