package io.github.pangju666.framework.data.mongodb

import io.github.pangju666.framework.data.mongodb.repository.SimpleBaseMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation
import spock.lang.Specification

import java.util.regex.Pattern

@SpringBootTest(classes = TestApplication)
class SimpleBaseMongoRepositorySpec extends Specification {
	@Autowired
	MongoOperations mongoOps

	SimpleBaseMongoRepository<UserDocument, Long> repo

	def setup() {
		// ... existing code ...
		mongoOps.dropCollection(UserDocument)
		mongoOps.save(new UserDocument(1L, "Alice", 30, "alice@example.com"))
		mongoOps.save(new UserDocument(2L, "Bob", 25, null))
		mongoOps.save(new UserDocument(3L, "Alice", 28, "alice2@example.com"))
		mongoOps.save(new UserDocument(4L, "Carol", 25, "carol@example.com"))

		// 强制将 Bob 的 email 显式设为 BSON Null（字段存在且为 null）
		mongoOps.updateMulti(
			new Query(Criteria.where("name").is("Bob")),
			new Update().set("email", null),
			UserDocument
		)

		// 直接构造 SimpleBaseMongoRepository
		def mappingContext = mongoOps.getConverter().getMappingContext()
		def entity = mappingContext.getRequiredPersistentEntity(UserDocument)
		MongoEntityInformation<UserDocument, Long> metadata =
			new MappingMongoEntityInformation<>(entity, "users")
		repo = new SimpleBaseMongoRepository<>(metadata, mongoOps)
		// ... existing code ...
	}

	def cleanup() {
		mongoOps.dropCollection(UserDocument)
	}

	def "exists / findOne / count 基本校验"() {
		expect:
		repo.existsByKeyValue("name", "Alice")
		repo.exists(Query.query(Criteria.where("age").is(25)))

		and:
		repo.findOneByKeyValue("name", "Alice").present
		repo.findOne(Query.query(Criteria.where("name").is("Carol"))).present

		and:
		repo.count(Query.query(Criteria.where("name").is("Alice"))) == 2
	}

	def "distinct 与 findAllByKeyValues / NotValues / Null / NotNull"() {
		when:
		def distinctNames = repo.findDistinctKeyValues("name", String)

		then:
		new HashSet<>(distinctNames) == ["Alice", "Bob", "Carol"] as Set

		when:
		def inList = repo.findAllByKeyValues("name", ["Alice", "Bob"])

		then:
		inList.size() == 3

		when:
		def ninList = repo.findAllByKeyNotValues("name", ["Alice"])

		then:
		ninList.size() == 2
		new HashSet<>(ninList*.name) == ["Bob", "Carol"] as Set

		when:
		def nullEmail = repo.findAllByKeyNull("email")
		def notNullEmail = repo.findAllByKeyNotNull("email")

		then:
		nullEmail*.name == ["Bob"]
		new HashSet<>(notNullEmail*.name) == ["Alice", "Carol"] as Set
	}

	def "regex / notRegex 查询"() {
		when:
		def startsWithA = repo.findAllByKeyRegex("name", "^A.*")

		then:
		new HashSet<>(startsWithA*.name) == ["Alice"] as Set

		when:
		def notEndsWithCe = repo.findAllByKeyNotRegex("name", ".*ce\$")

		then:
		new HashSet<>(notEndsWithCe*.name) == ["Bob", "Carol"] as Set
	}

	def "分页查询 findAll(pageable, query)"() {
		given:
		def pageable = PageRequest.of(0, 2)
		def query = new Query()

		when:
		def page = repo.findAll(pageable, query)

		then:
		page.totalElements == 4
		page.size == 2
		page.content.size() == 2
	}

	def "更新：updateById / updateAllById / updateAllByKeyValue / updateAll / replaceKeyValue"() {
		when: "updateById 修改单条"
		repo.updateById(new Update().set("email", "alice-new@example.com"), 1L)

		then:
		repo.findOneByKeyValue("id", 1L).get().email == "alice-new@example.com"

		when: "updateAllById 批量修改 age"
		repo.updateAllById(new Update().set("age", 26), [2L, 3L])

		then:
		new HashSet<>(repo.findAllByKeyValues("id", [2L, 3L])*.age) == [26] as Set

		when: "updateAllByKeyValue 按 name 批量更新"
		repo.updateAllByKeyValue(new Update().set("age", 31), "name", "Alice")

		then:
		new HashSet<>(repo.findAllByKeyValue("name", "Alice")*.age) == [31] as Set

		when: "updateAll：按自定义查询批量更新"
		repo.updateAll(new Update().set("email", null), new Query(Criteria.where("name").is("Carol")))

		then:
		repo.findOneByKeyValue("name", "Carol").get().email == null

		when: "replaceKeyValue：替换 Bob -> Bobby"
		repo.replaceKeyValue("name", "Bob", "Bobby")

		then:
		repo.findOneByKeyValue("name", "Bob").empty
		repo.findOneByKeyValue("name", "Bobby").present
	}

	def "删除：deleteAllByKeyValue / deleteAll(query)"() {
		when:
		repo.deleteAllByKeyValue("name", "Carol")

		then:
		repo.findOneByKeyValue("name", "Carol").empty

		when:
		repo.deleteAll(new Query(Criteria.where("name").is("Alice")))

		then:
		repo.findAllByKeyValue("name", "Alice").isEmpty()
	}

	def "边界：空集合/空查询安全处理"() {
		when:
		def emptyIn = repo.findAllByKeyValues("name", [])
		def emptyNin = repo.findAllByKeyNotValues("name", [])

		then:
		emptyIn.isEmpty()
		emptyNin.isEmpty()

		when:
		def distinctWithQuery = repo.findDistinctKeyValues(new Query(Criteria.where("age").is(25)), "name", String)

		then:
		new HashSet<>(distinctWithQuery) == ["Bob", "Carol"] as Set
	}

	def "regex Pattern 重载：findAllByKeyRegex(key, Pattern)"() {
		when:
		def pattern = Pattern.compile("^A.*")
		def result = repo.findAllByKeyRegex("name", pattern)

		then:
		new HashSet<>(result*.name) == ["Alice"] as Set
	}

	def "notRegex Pattern 重载：findAllByKeyNotRegex(key, Pattern)"() {
		when:
		def pattern = Pattern.compile(".*ce\$")
		def result = repo.findAllByKeyNotRegex("name", pattern)

		then:
		new HashSet<>(result*.name) == ["Bob", "Carol"] as Set
	}

	def "findAll(null) 返回空列表"() {
		when:
		Query query = null
		def list = repo.findAll(query as Query)

		then:
		list.isEmpty()
	}
}
