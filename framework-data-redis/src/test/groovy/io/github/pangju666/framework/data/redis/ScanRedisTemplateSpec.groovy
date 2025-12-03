package io.github.pangju666.framework.data.redis

import io.github.pangju666.framework.data.redis.core.ScanRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.data.redis.serializer.RedisSerializer
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@SpringBootTest
@Stepwise
class ScanRedisTemplateSpec extends Specification {
	@Autowired
	StringRedisTemplate redisTemplate

	ScanRedisTemplate<String> scanRedisTemplate

	def setup() {
		scanRedisTemplate = new ScanRedisTemplate<>(redisTemplate)
		scanRedisTemplate.afterPropertiesSet()

		// 清理并准备数据
		def conn = scanRedisTemplate.getConnectionFactory().getConnection()
		conn.serverCommands().flushAll()

		// String keys
		scanRedisTemplate.opsForValue().set("user:1:name", "Alice")
		scanRedisTemplate.opsForValue().set("user:2:email", "bob@example.com")
		scanRedisTemplate.opsForValue().set("order:1001:status", "PAID")

		// Set
		scanRedisTemplate.opsForSet().add("tags:set", "java", "redis", "spring", "groovy")

		// ZSet
		scanRedisTemplate.opsForZSet().add("scores:zset", "alice", 10D)
		scanRedisTemplate.opsForZSet().add("scores:zset", "bob", 5D)
		scanRedisTemplate.opsForZSet().add("scores:zset", "charlie", 20D)

		// Hash
		scanRedisTemplate.opsForHash().put("user:1:profile", "name", "Alice")
		scanRedisTemplate.opsForHash().put("user:1:profile", "email", "alice@example.com")
		scanRedisTemplate.opsForHash().put("user:1:profile", "age", "30")
	}

	def "模板初始化：键/哈希键序列化器固定为 String，且不可修改"() {
		when: "尝试外部修改键/哈希键序列化器"
		scanRedisTemplate.setKeySerializer(RedisSerializer.java())
		scanRedisTemplate.setHashKeySerializer(RedisSerializer.java())

		then: "仍然是 StringRedisSerializer"
		scanRedisTemplate.getKeySerializer().getClass().simpleName == "StringRedisSerializer"
		scanRedisTemplate.getHashKeySerializer().getClass().simpleName == "StringRedisSerializer"
	}

	def "键扫描：scanKeys() 返回所有键"() {
		when:
		def keys = scanRedisTemplate.scanKeys()

		then:
		keys.containsAll([
			"user:1:name",
			"user:2:email",
			"order:1001:status",
			"tags:set",
			"scores:zset",
			"user:1:profile"
		] as Set)
	}

	@Unroll
	def "键扫描：scanKeys(DataType=#dataType) 按类型过滤"() {
		expect:
		scanRedisTemplate.scanKeys(dataType) == expected as Set

		where:
		dataType        || expected
		DataType.STRING || ["user:1:name", "user:2:email", "order:1001:status"]
		DataType.SET    || ["tags:set"]
		DataType.ZSET   || ["scores:zset"]
		DataType.HASH   || ["user:1:profile"]
		DataType.NONE   || ["user:1:name", "user:2:email", "order:1001:status", "tags:set", "scores:zset", "user:1:profile"]
	}

	def "键扫描：scanKeys(ScanOptions) 按模式匹配与 count/type 组合"() {
		when: "匹配 user:* 且建议 count=100"
		def options = ScanOptions.scanOptions().match("user:*").count(100).build()
		def keys = scanRedisTemplate.scanKeys(options)

		then:
		keys == ["user:1:name", "user:2:email", "user:1:profile"] as Set

		when: "匹配 *:zset 且类型过滤 ZSET"
		options = scanRedisTemplate.scanOptions("*:zset", DataType.ZSET, 10L)
		keys = scanRedisTemplate.scanKeys(options)

		then:
		keys == ["scores:zset"] as Set
	}

	def "键扫描：按后缀/前缀/关键字匹配"() {
		expect: "后缀"
		scanRedisTemplate.scanKeysBySuffix("status") == ["order:1001:status"] as Set

		and: "前缀"
		scanRedisTemplate.scanKeysByPrefix("user:") == ["user:1:name", "user:2:email", "user:1:profile"] as Set

		and: "关键字"
		scanRedisTemplate.scanKeysByKeyword(":1:") == ["user:1:name", "user:1:profile"] as Set
	}

	def "键扫描：空白前缀/后缀/关键字返回空集合"() {
		expect:
		scanRedisTemplate.scanKeysBySuffix("") == [] as Set
		scanRedisTemplate.scanKeysByPrefix("   ") == [] as Set
		scanRedisTemplate.scanKeysByKeyword(null) == [] as Set
	}

	def "Hash 扫描：scanHash(指定选项) 仅作用于哈希字段名模式"() {
		when: "匹配字段名前缀 'na*'"
		def options = scanRedisTemplate.scanOptions("na*", null, null)
		def result = scanRedisTemplate.scanHash("user:1:profile", options)

		then: "只返回 name 字段"
		result == ["name": "Alice"]
	}

	def "Hash 扫描：按后缀/前缀/关键字过滤字段名"() {
		expect:
		scanRedisTemplate.scanHashBySuffix("user:1:profile", "ge") == ["age": "30"]
		scanRedisTemplate.scanHashByPrefix("user:1:profile", "em") == ["email": "alice@example.com"]
		scanRedisTemplate.scanHashByKeyword("user:1:profile", "am") == ["name": "Alice"]
	}

	def "Hash 扫描：空白后缀/前缀/关键字返回空映射"() {
		expect:
		scanRedisTemplate.scanHashBySuffix("user:1:profile", "") == [:]
		scanRedisTemplate.scanHashByPrefix("user:1:profile", "   ") == [:]
		scanRedisTemplate.scanHashByKeyword("user:1:profile", null) == [:]
	}

	def "Set 扫描：无模式返回所有元素；带模式按成员名过滤"() {
		when:
		def all = scanRedisTemplate.scanSet("tags:set", ScanOptions.NONE)

		then:
		all.containsAll(["java", "redis", "spring", "groovy"] as Set)

		when:
		def filtered = scanRedisTemplate.scanSet("tags:set",
			scanRedisTemplate.scanOptions("spr*", null, null))

		then:
		filtered == ["spring"] as Set
	}

	def "ZSet 扫描：返回按默认比较排序的 TypedTuple；带模式按成员过滤"() {
		when:
		SortedSet<ZSetOperations.TypedTuple<String>> tuples =
			scanRedisTemplate.scanZSet("scores:zset", ScanOptions.NONE)

		then: "按分数默认比较排序（5,10,20）"
		tuples*.getValue() == ["bob", "alice", "charlie"]
		tuples*.getScore() == [5D, 10D, 20D]

		when:
		def filtered = scanRedisTemplate.scanZSet("scores:zset",
			scanRedisTemplate.scanOptions("b*", null, null))

		then: "成员名匹配 b* 只返回 bob"
		filtered*.getValue() == ["bob"]
	}

	def "Set/ZSet 扫描：当提供模式且值序列化器不支持 String 时抛 UnsupportedOperationException"() {
		given: "一个值序列化器显式声明不支持 String 的模板"
		def denyStringSerializer = new DenyStringSerializer()
		def badTemplate = new ScanRedisTemplate<String>()
		badTemplate.setConnectionFactory(scanRedisTemplate.getConnectionFactory())
		badTemplate.setValueSerializer(denyStringSerializer)
		badTemplate.afterPropertiesSet()

		when: "Set 使用模式"
		badTemplate.scanSet("tags:set", badTemplate.scanOptions("g*", null, null))

		then:
		thrown(UnsupportedOperationException)

		when: "ZSet 使用模式"
		badTemplate.scanZSet("scores:zset", badTemplate.scanOptions("c*", null, null))

		then:
		thrown(UnsupportedOperationException)
	}

	def "参数校验：scanKeys(null) 抛 IllegalArgumentException"() {
		when:
		scanRedisTemplate.scanKeys(null as ScanOptions)

		then:
		thrown(IllegalArgumentException)
	}

	def "参数校验：scanZSet 空 key / null options 抛 IllegalArgumentException"() {
		when:
		scanRedisTemplate.scanZSet("   ", ScanOptions.NONE)

		then:
		thrown(IllegalArgumentException)

		when:
		scanRedisTemplate.scanZSet("scores:zset", null)

		then:
		thrown(IllegalArgumentException)
	}

	def "参数校验：scanSet 空 key / null options 抛 IllegalArgumentException"() {
		when:
		scanRedisTemplate.scanSet("", ScanOptions.NONE)

		then:
		thrown(IllegalArgumentException)

		when:
		scanRedisTemplate.scanSet("tags:set", null)

		then:
		thrown(IllegalArgumentException)
	}

	def "参数校验：scanHash 空 key / null options 抛 IllegalArgumentException"() {
		when:
		scanRedisTemplate.scanHash(" ", ScanOptions.NONE)

		then:
		thrown(IllegalArgumentException)

		when:
		scanRedisTemplate.scanHash("user:1:profile", null)

		then:
		thrown(IllegalArgumentException)
	}

	def "scanOptions 构建：pattern/type/count 组合生效"() {
		when: "仅设置 count"
		def opt1 = scanRedisTemplate.scanOptions(null, null, 50L)
		def keys1 = scanRedisTemplate.scanKeys(opt1)

		then: "count 为提示，不保证数量；功能正确"
		keys1.containsAll(["user:1:name", "user:2:email", "order:1001:status", "tags:set", "scores:zset", "user:1:profile"])

		when: "设置 pattern 与 type"
		def opt2 = scanRedisTemplate.scanOptions("user:*", DataType.HASH, null)
		def keys2 = scanRedisTemplate.scanKeys(opt2)

		then: "只匹配到 user:* 且类型为 HASH 的键"
		keys2 == ["user:1:profile"] as Set
	}

	/**
	 * 显式拒绝 String 的值序列化器，用于触发 UnsupportedOperationException 分支。
	 */
	static class DenyStringSerializer implements RedisSerializer<Object> {
		@Override
		byte[] serialize(Object o) {
			return o == null ? null : o.toString().getBytes()
		}

		@Override
		Object deserialize(byte[] bytes) {
			return bytes == null ? null : new String(bytes)
		}

		@Override
		boolean canSerialize(Class<?> type) {
			// 显式不支持 String，用来触发 ScanRedisTemplate 的 UOE 检查
			return type != String.class
		}
	}
}
