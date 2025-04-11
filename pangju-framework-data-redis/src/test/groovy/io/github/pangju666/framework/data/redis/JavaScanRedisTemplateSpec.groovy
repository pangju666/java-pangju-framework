package io.github.pangju666.framework.data.redis

import io.github.pangju666.framework.data.redis.bean.JavaScanRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.core.ScanOptions
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class JavaScanRedisTemplateSpec extends Specification {
	@Autowired
	JavaScanRedisTemplate redisTemplate

	def setup() {
		// 字符串类型
		redisTemplate.opsForValue().set("test:string:1", "value1")
		redisTemplate.opsForValue().set("test:string:2", "value2")

		// Hash类型
		redisTemplate.opsForHash().put("test:hash", "field1", "value1")
		redisTemplate.opsForHash().put("test:hash", "field2", "value2")

		// Set类型
		redisTemplate.opsForSet().add("test:set", "member1", "member2")

		// ZSet类型
		redisTemplate.opsForZSet().add("test:zset", "member1", 1.0)
		redisTemplate.opsForZSet().add("test:zset", "member2", 2.0)
	}

	def cleanup() {
		redisTemplate.delete("test:string:1")
		redisTemplate.delete("test:string:2")
		redisTemplate.delete("test:hash")
		redisTemplate.delete("test:set")
		redisTemplate.delete("test:zset")
	}

	@Unroll
	def "测试按前缀扫描键 - #prefix"() {
		when: "使用前缀扫描键"
		def result = redisTemplate.scanKeysByPrefix(prefix)

		then: "验证结果"
		result.size() == expectedSize
		result.containsAll(expectedKeys)

		where:
		prefix        | expectedSize | expectedKeys
		"test:string" | 2            | ["test:string:1", "test:string:2"]
		"test:hash"   | 1            | ["test:hash"]
		"nonexistent" | 0            | []
	}

	@Unroll
	def "测试按后缀扫描键 - #suffix"() {
		when: "使用后缀扫描键"
		def result = redisTemplate.scanKeysBySuffix(suffix)

		then: "验证结果"
		result.size() == expectedSize
		result.containsAll(expectedKeys)

		where:
		suffix  | expectedSize | expectedKeys
		":1"    | 1            | ["test:string:1"]
		":2"    | 1            | ["test:string:2"]
		"wrong" | 0            | []
	}

	def "测试按数据类型扫描键"() {
		when: "扫描不同数据类型的键"
		def stringKeys = redisTemplate.scanKeysByDataType(DataType.STRING)
		def hashKeys = redisTemplate.scanKeysByDataType(DataType.HASH)
		def setKeys = redisTemplate.scanKeysByDataType(DataType.SET)
		def zsetKeys = redisTemplate.scanKeysByDataType(DataType.ZSET)

		then: "验证各类型的键数量"
		stringKeys.size() == 2
		hashKeys.size() == 1
		setKeys.size() == 1
		zsetKeys.size() == 1
	}

	def "测试扫描ZSet成员"() {
		given: "准备ZSet测试数据"
		def key = "test:zset"

		when: "扫描所有ZSet成员"
		def result = redisTemplate.scanZSetValues(key)

		then: "验证结果"
		result.size() == 2
		result.collect { it.value() }.containsAll(["member2", "member1"])
		result.collect { it.score() }.containsAll(List.of(2.0d, 1.0d))
	}

	def "测试扫描Set成员"() {
		given: "准备Set测试数据"
		def key = "test:set"

		when: "扫描所有Set成员"
		def result = redisTemplate.scanSetValues(key)

		then: "验证结果"
		result.size() == 2
		result.containsAll(["member1", "member2"])
	}

	def "测试扫描Hash字段"() {
		given: "准备Hash测试数据"
		def key = "test:hash"

		when: "扫描所有Hash字段"
		def result = redisTemplate.scanHashValues(key)

		then: "验证结果"
		result.size() == 2
		result.keySet().containsAll(["field1", "field2"])
		result.values().containsAll(["value1", "value2"])
	}

	def "测试使用自定义扫描选项"() {
		given: "创建扫描选项"
		def options = ScanOptions.scanOptions()
			.match("test:string:*")
			.count(1)
			.build()

		when: "使用自定义选项扫描"
		def result = redisTemplate.scanKeys(options)

		then: "验证结果"
		result.size() == 2
		result.containsAll(["test:string:1", "test:string:2"])
	}
}
