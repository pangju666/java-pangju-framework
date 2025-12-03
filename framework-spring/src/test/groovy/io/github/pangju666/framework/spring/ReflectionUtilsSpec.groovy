package io.github.pangju666.framework.spring

import io.github.pangju666.framework.spring.utils.ReflectionUtils
import spock.lang.Specification
import spock.lang.Unroll

class ReflectionUtilsSpec extends Specification {

	def "getField 可访问字段直接读取"() {
		given: "具有 public 字段的对象"
		def user = new ReflectionFixtures.User()
		user.nickname = "Nick"

		when: "直接读取可访问字段"
		def value = ReflectionUtils.getField(user, "nickname")

		then: "返回字段值"
		value == "Nick"
	}

	def "getField 不可访问字段回退到 getter"() {
		given: "具有 private 字段且存在 getter 的对象"
		def user = new ReflectionFixtures.User()
		user.setName("张三")

		when: "读取不可访问字段，回退到 getter"
		def value = ReflectionUtils.getField(user, "name")

		then: "返回 getter 的结果"
		value == "张三"
	}

	def "getField 指定类型读取，私有字段回退到 getter"() {
		given:
		def user = new ReflectionFixtures.User()
		user.setAge(30)

		when:
		Integer age = ReflectionUtils.getField(user, "age", Integer.class)

		then:
		age == 30
	}

	@Unroll
	def "getField 参数校验异常: target=#t, fieldName='#f', type=#type"() {
		when:
		ReflectionUtils.getField(t, f, type as Class)

		then:
		thrown(expected)

		where:
		t                             | f     | type    || expected
		null                          | "x"   | Integer || IllegalArgumentException
		new ReflectionFixtures.User() | ""    | Integer || IllegalArgumentException
		new ReflectionFixtures.User() | "  "  | Integer || IllegalArgumentException
		new ReflectionFixtures.User() | "age" | null    || IllegalArgumentException
	}

	def "getField 字段不存在或不可访问且无 getter 时抛出 IllegalStateException"() {
		given:
		def obj = new ReflectionFixtures.NoAccessor()

		when: "字段不存在"
		ReflectionUtils.getField(obj, "missing")

		then:
		thrown(IllegalStateException)

		when: "字段存在但不可访问且无 getter"
		ReflectionUtils.getField(obj, "hidden")

		then:
		thrown(IllegalStateException)
	}

	def "setField 可访问字段直接写入"() {
		given:
		def user = new ReflectionFixtures.User()

		when:
		ReflectionUtils.setField(user, "nickname", "Nick")

		then:
		user.nickname == "Nick"
	}

	def "setField 指定类型写入可访问字段"() {
		given:
		def user = new ReflectionFixtures.User()

		when:
		ReflectionUtils.setField(user, "nickname", "Nick", String.class)

		then:
		user.nickname == "Nick"
	}

	def "setField 私有字段存在 setter 时当前实现仍抛出 IllegalStateException（缺陷覆盖）"() {
		given:
		def user = new ReflectionFixtures.User()

		when: "尝试通过 setter 回退设置私有字段"
		ReflectionUtils.setField(user, "name", "张三")

		then: "由于 findMethod 使用无参签名，无法匹配 setter，抛出 IllegalStateException"
		thrown(IllegalStateException)
	}

	def "setField 字段不存在或不可访问且无 setter 时抛出 IllegalStateException"() {
		given:
		def obj = new ReflectionFixtures.NoAccessor()

		when: "字段不存在"
		ReflectionUtils.setField(obj, "missing", "x")

		then:
		thrown(IllegalStateException)

		when: "字段存在但不可访问且无 setter"
		ReflectionUtils.setField(obj, "hidden", "x")

		then:
		thrown(IllegalStateException)
	}

	@Unroll
	def "setField 参数校验异常: target=#t, fieldName='#f'"() {
		when:
		ReflectionUtils.setField(t, f, "x")

		then:
		thrown(IllegalArgumentException)

		where:
		t                             | f
		null                          | "x"
		new ReflectionFixtures.User() | ""
		new ReflectionFixtures.User() | "  "
	}

	def "getClassGenericType 默认第一个泛型类型解析"() {
		expect:
		ReflectionUtils.getClassGenericType(ReflectionFixtures.GenericChild.class) == String.class
	}

	def "getClassGenericType 指定索引泛型类型解析"() {
		expect:
		ReflectionUtils.getClassGenericType(ReflectionFixtures.GenericChild.class, 0) == String.class
	}

	def "getClassGenericType 非参数化父类或索引越界返回 null"() {
		expect:
		ReflectionUtils.getClassGenericType(Object.class) == null
		ReflectionUtils.getClassGenericType(ReflectionFixtures.GenericChild.class, 1) == null
	}

	def "canMakeAccessible 对私有字段返回 true 并设置为可访问"() {
		given:
		def user = new ReflectionFixtures.User()
		def field = ReflectionFixtures.User.class.getDeclaredField("name")

		expect:
		!field.canAccess(user)

		when:
		def changed = ReflectionUtils.canMakeAccessible(field)

		then:
		changed
		field.canAccess(user)
	}

	def "canMakeAccessible 对私有方法返回 true 并设置为可访问"() {
		given:
		def user = new ReflectionFixtures.User()
		def method = ReflectionFixtures.User.class.getDeclaredMethod("secret")

		expect:
		!method.canAccess(user)

		when:
		def changed = ReflectionUtils.canMakeAccessible(method)

		then:
		changed
		method.canAccess(user)
	}

	def "canMakeAccessible 成员已可访问时返回 false"() {
		given:
		def user = new ReflectionFixtures.User()
		def pubField = ReflectionFixtures.User.class.getDeclaredField("nickname")
		def pubMethod = ReflectionFixtures.User.class.getDeclaredMethod("getName")

		expect:
		pubField.canAccess(user)
		pubMethod.canAccess(user)

		when:
		def fieldChanged = ReflectionUtils.canMakeAccessible(pubField)
		def methodChanged = ReflectionUtils.canMakeAccessible(pubMethod)

		then:
		!fieldChanged
		!methodChanged
	}
}
