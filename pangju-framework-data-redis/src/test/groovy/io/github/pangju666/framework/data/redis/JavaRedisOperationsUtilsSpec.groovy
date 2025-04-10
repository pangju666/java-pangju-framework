package io.github.pangju666.framework.data.redis

import io.github.pangju666.framework.data.redis.bean.JavaRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class JavaRedisOperationsUtilsSpec extends Specification {
	@Autowired
	JavaRedisTemplate javaRedisTemplate

	def "测试键组合方法"() {
		expect: "组合多个键应返回正确的结果"
		RedisOperationsUtils.computeKey(keys as String[]) == expected

		where:
		keys                   | expected
		["test"]               | "test"
		["user", "1"]          | "user::1"
		["order", "1", "item"] | "order::1::item"
	}


}
