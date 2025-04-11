package io.github.pangju666.framework.data.redis


import io.github.pangju666.framework.data.redis.bean.JsonScanRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.core.ScanOptions
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class JsonScanRedisTemplateSpec extends Specification {
	@Autowired
	JsonScanRedisTemplate redisTemplate

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

	def "测试键扫描操作"() {
		given: "准备测试数据"
		def testObj1 = new TestObject(name: "test1", value: 100)
		def testObj2 = new TestObject(name: "test2", value: 200)
		redisTemplate.opsForValue().set("java:test:key1", testObj1)
		redisTemplate.opsForValue().set("java:test:key2", testObj2)
		redisTemplate.opsForValue().set("other:key", "value")

		expect: "测试各种键扫描方法"
		redisTemplate.scanKeysByPrefix("java:test").size() == 2
		redisTemplate.scanKeysBySuffix("key1").size() == 1
		redisTemplate.scanKeysByKeyword("test").size() == 2
		redisTemplate.scanKeysByDataType(DataType.STRING).size() == 3
		redisTemplate.scanKeys().size() >= 3

		cleanup:
		redisTemplate.delete("java:test:key1")
		redisTemplate.delete("java:test:key2")
		redisTemplate.delete("other:key")
	}

	def "测试ZSet扫描操作"() {
		given: "准备测试数据"
		def key = "test:java:zset"
		def testObj1 = new TestObject(name: "member1", value: 100)
		def testObj2 = new TestObject(name: "member2", value: 200)
		redisTemplate.opsForZSet().add(key, testObj1, 1.0)
		redisTemplate.opsForZSet().add(key, testObj2, 2.0)

		expect: "测试各种ZSet扫描方法"
		redisTemplate.scanZSetValuesByPrefix(key, "mem").size() == 2
		redisTemplate.scanZSetValuesBySuffix(key, "1").size() == 1
		redisTemplate.scanZSetValuesByKeyword(key, "member").size() == 2
		redisTemplate.scanZSetValues(key).size() == 2

		and: "验证排序"
		def values = redisTemplate.scanZSetValues(key)
		values.first().score() > values.last().score()

		cleanup:
		redisTemplate.delete(key)
	}

	def "测试Set扫描操作"() {
		given: "准备测试数据"
		def key = "test:java:set"
		def testObj1 = new TestObject(name: "member1", value: 100)
		def testObj2 = new TestObject(name: "member2", value: 200)
		redisTemplate.opsForSet().add(key, testObj1, testObj2)

		expect: "测试各种Set扫描方法"
		redisTemplate.scanSetValuesByPrefix(key, "mem").size() == 2
		redisTemplate.scanSetValuesBySuffix(key, "1").size() == 1
		redisTemplate.scanSetValuesByKeyword(key, "member").size() == 2
		redisTemplate.scanSetValues(key).size() == 2

		cleanup:
		redisTemplate.delete(key)
	}

	def "测试Hash扫描操作"() {
		given: "准备测试数据"
		def key = "test:java:hash"
		def testObj1 = new TestObject(name: "value1", value: 100)
		def testObj2 = new TestObject(name: "value2", value: 200)
		redisTemplate.opsForHash().put(key, "field1", testObj1)
		redisTemplate.opsForHash().put(key, "field2", testObj2)

		expect: "测试各种Hash扫描方法"
		redisTemplate.scanHashValuesByPrefix(key, "field").size() == 2
		redisTemplate.scanHashValuesBySuffix(key, "1").size() == 1
		redisTemplate.scanHashValuesByKeyword(key, "field").size() == 2
		redisTemplate.scanHashValues(key).size() == 2

		cleanup:
		redisTemplate.delete(key)
	}

	def "测试自定义扫描选项"() {
		given: "准备测试数据"
		def testObj = new TestObject(name: "test", value: 100)
		redisTemplate.opsForValue().set("custom:test:key", testObj)
		def hashKey = "custom:test:hash"
		redisTemplate.opsForHash().put(hashKey, "field", testObj)
		def setKey = "custom:test:set"
		redisTemplate.opsForSet().add(setKey, testObj)
		def zsetKey = "custom:test:zset"
		redisTemplate.opsForZSet().add(zsetKey, testObj, 1.0)

		when: "使用自定义扫描选项"
		def scanOptions = ScanOptions.scanOptions().match("custom:*").count(100).build()

		then: "验证各种类型的扫描结果"
		redisTemplate.scanKeys(scanOptions).size() >= 4
		redisTemplate.scanHashValues(hashKey, scanOptions).size() == 1
		redisTemplate.scanSetValues(setKey, scanOptions).size() == 1
		redisTemplate.scanZSetValues(zsetKey, scanOptions).size() == 1

		cleanup:
		redisTemplate.delete("custom:test:key")
		redisTemplate.delete(hashKey)
		redisTemplate.delete(setKey)
		redisTemplate.delete(zsetKey)
	}

	def "测试边界条件处理"() {
		expect: "测试空参数和null处理"
		redisTemplate.scanKeysByPrefix("") == Collections.emptySet()
		redisTemplate.scanKeysBySuffix("") == Collections.emptySet()
		redisTemplate.scanKeysByKeyword("") == Collections.emptySet()
		redisTemplate.scanKeysByDataType(null) == Collections.emptySet()
		redisTemplate.scanKeys(null) == Collections.emptySet()

		and: "测试不存在的键"
		redisTemplate.scanZSetValues("non:exist:key").isEmpty()
		redisTemplate.scanSetValues("non:exist:key").isEmpty()
		redisTemplate.scanHashValues("non:exist:key").isEmpty()
	}

	static class TestObject implements Serializable {
		String name
		int value
	}
}
