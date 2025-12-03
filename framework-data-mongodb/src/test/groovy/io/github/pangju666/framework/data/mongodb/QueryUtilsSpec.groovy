package io.github.pangju666.framework.data.mongodb

import io.github.pangju666.framework.data.mongodb.utils.QueryUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern

@SpringBootTest(classes = TestApplication)
class QueryUtilsSpec extends Specification {

	@Autowired
	MongoOperations mongoOps

	def setup() {
		mongoOps.dropCollection(UserDocument)
		// 初始化数据：Alice(2条), Bob(显式null), Carol(非空), Dave(显式null)
		mongoOps.save(new UserDocument(1L, "Alice", 30, "alice@example.com"))
		mongoOps.save(new UserDocument(2L, "Bob", 25, null))
		mongoOps.save(new UserDocument(3L, "Alice", 28, "alice2@example.com"))
		mongoOps.save(new UserDocument(4L, "Carol", 25, "carol@example.com"))
		mongoOps.save(new UserDocument(5L, "Dave", 40, null))

		// 将 Bob、Dave 的 email 显式设为 BSON Null（避免“缺失字段”的不确定性）
		mongoOps.updateMulti(new Query(Criteria.where("name").is("Bob")), new Update().set("email", null), UserDocument)
		mongoOps.updateMulti(new Query(Criteria.where("name").is("Dave")), new Update().set("email", null), UserDocument)
	}

	def cleanup() {
		mongoOps.dropCollection(UserDocument)
	}

	def "queryByKeyNull / queryByKeyNotNull 行为验证（显式null与非空分离）"() {
		when:
		def qNull = QueryUtils.queryByKeyNull("email")
		def qNotNull = QueryUtils.queryByKeyNotNull("email")

		def nullList = mongoOps.find(qNull, UserDocument)
		def notNullList = mongoOps.find(qNotNull, UserDocument)

		then:
		new HashSet<>(nullList*.name) == ["Bob", "Dave"] as Set
		new HashSet<>(notNullList*.name) == ["Alice", "Carol"] as Set
	}

	def "queryByKeyValue / queryByKeyNotValue：非空值与空值分支"() {
		when: "等值匹配非空"
		def qAlice = QueryUtils.queryByKeyValue("name", "Alice")
		def resAlice = mongoOps.find(qAlice, UserDocument)

		then:
		resAlice.size() == 2
		new HashSet<>(resAlice*.email) == ["alice@example.com", "alice2@example.com"] as Set

		when: "等值匹配空值：返回“为空或不存在”的集合（此处两条显式null）"
		def qEmailNull = QueryUtils.queryByKeyValue("email", null)
		def resEmailNull = mongoOps.find(qEmailNull, UserDocument)

		then:
		new HashSet<>(resEmailNull*.name) == ["Bob", "Dave"] as Set

		when: "不等值匹配非空"
		def qNameNotAlice = QueryUtils.queryByKeyNotValue("name", "Alice")
		def resNameNotAlice = mongoOps.find(qNameNotAlice, UserDocument)

		then:
		new HashSet<>(resNameNotAlice*.name) == ["Bob", "Carol", "Dave"] as Set

		when: "不等值匹配空值：返回“非空且存在”的集合"
		def qEmailNotNull = QueryUtils.queryByKeyNotValue("email", null)
		def resEmailNotNull = mongoOps.find(qEmailNotNull, UserDocument)

		then:
		new HashSet<>(resEmailNotNull*.name) == ["Alice", "Carol"] as Set
	}

	def "queryByKeyValues / queryByKeyNotValues：集合包含与排除"() {
		when:
		def qIn = QueryUtils.queryByKeyValues("name", ["Alice", "Bob"])
		def resIn = mongoOps.find(qIn, UserDocument)

		then:
		resIn.size() == 3
		new HashSet<>(resIn*.name) == ["Alice", "Bob"] as Set

		when:
		def qNin = QueryUtils.queryByKeyNotValues("name", ["Alice"])
		def resNin = mongoOps.find(qNin, UserDocument)

		then:
		new HashSet<>(resNin*.name) == ["Bob", "Carol", "Dave"] as Set
	}

	def "queryByKeyRegex / queryByKeyNotRegex：字符串重载"() {
		when:
		def qStartsWithA = QueryUtils.queryByKeyRegex("name", "^A.*")
		def resStartsWithA = mongoOps.find(qStartsWithA, UserDocument)

		then:
		new HashSet<>(resStartsWithA*.name) == ["Alice"] as Set

		when:
		def qNotEndsWithCe = QueryUtils.queryByKeyNotRegex("name", ".*ce\$")
		def resNotEndsWithCe = mongoOps.find(qNotEndsWithCe, UserDocument)

		then:
		new HashSet<>(resNotEndsWithCe*.name) == ["Bob", "Carol", "Dave"] as Set
	}

	def "queryByKeyRegex / queryByKeyNotRegex：Pattern 重载"() {
		when:
		def qStartsWithA = QueryUtils.queryByKeyRegex("name", Pattern.compile("^A.*"))
		def resStartsWithA = mongoOps.find(qStartsWithA, UserDocument)

		then:
		new HashSet<>(resStartsWithA*.name) == ["Alice"] as Set

		when:
		def qNotEndsWithCe = QueryUtils.queryByKeyNotRegex("name", Pattern.compile(".*ce\$"))
		def resNotEndsWithCe = mongoOps.find(qNotEndsWithCe, UserDocument)

		then:
		new HashSet<>(resNotEndsWithCe*.name) == ["Bob", "Carol", "Dave"] as Set
	}

	@Unroll
	def "参数校验：非法参数应抛 IllegalArgumentException —— #caseDesc"() {
		when:
		action.call()

		then:
		thrown(IllegalArgumentException)

		where:
		caseDesc       | action
		"空 key(null)" | { QueryUtils.queryByKeyNull(null) }
		"空 key('')"   | { QueryUtils.queryByKeyNull("") }
		"空 regex('')" | { QueryUtils.queryByKeyRegex("name", "") }
		"空集合([])"   | { QueryUtils.queryByKeyValues("name", Collections.emptyList()) }
		"null Pattern" | { QueryUtils.queryByKeyRegex("name", (Pattern) null) }
	}

	def "findAll 与组合条件辅助：可直接用于 MongoOperations 执行"() {
		when:
		def q = new Query().addCriteria(
			new Criteria().andOperator(
				Criteria.where("age").gte(25),
				Criteria.where("name").regex("^A.*")
			)
		)
		def res = mongoOps.find(q, UserDocument)

		then:
		new HashSet<>(res*.name) == ["Alice"] as Set
	}
}
