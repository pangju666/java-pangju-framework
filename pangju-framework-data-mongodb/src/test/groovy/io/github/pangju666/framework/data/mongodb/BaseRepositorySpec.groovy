package io.github.pangju666.framework.data.mongodb

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import io.github.pangju666.framework.data.mongodb.model.TestDocument
import io.github.pangju666.framework.data.mongodb.repository.TestRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class BaseRepositorySpec extends Specification {
	@Autowired
	MongoOperations mongoOperations
	@Autowired
	TestRepository repository
	TestDocument testDoc

	def setup() {
		testDoc = new TestDocument(id: new ObjectId("67f8d48d520f597e6357647c"), name: "测试文档")
	}

	def "测试 exist 方法"() {
		given: "准备测试数据"
		def query = Query.query(Criteria.where("name").is("测试文档"))

		when: "调用方法"
		repository.exist(query)

		then: "验证交互"
		1 * mongoOperations.exists(query, TestDocument, _) >> true
	}

	def "测试 getByKeyValue 方法"() {
		given: "准备测试数据"
		def key = "name"
		def value = "测试"

		when: "调用方法"
		repository.getByKeyValue(key, value)

		then: "验证交互"
		1 * mongoOperations.findOne(_, TestDocument, _) >> testDoc
	}

	def "测试 getById 方法"() {
		given: "准备测试数据"
		def id = new ObjectId().toHexString()

		when: "调用方法"
		repository.getById(id)

		then: "验证交互"
		1 * mongoOperations.findById(id, TestDocument, _) >> testDoc
	}

	def "测试 get 方法"() {
		given: "准备测试数据"
		def query = new Query()

		when: "调用方法"
		repository.get(query)

		then: "验证交互"
		1 * mongoOperations.findOne(query, TestDocument, _) >> testDoc
	}

	def "测试 count 方法"() {
		when: "调用无参count方法"
		repository.count()

		then: "验证交互"
		1 * mongoOperations.count(_, _) >> 1L
	}

	def "测试 count(Query) 方法"() {
		given: "准备测试数据"
		def query = new Query()

		when: "调用带参数count方法"
		repository.count(query)

		then: "验证交互"
		1 * mongoOperations.count(query, _) >> 1L
	}

	def "测试 listDistinctKeyValues 方法"() {
		given: "准备测试数据"
		def key = "name"

		when: "调用方法"
		repository.listDistinctKeyValues(key, String)

		then: "验证交互"
		1 * mongoOperations.findDistinct(_, key, _, TestDocument, String) >> ["测试1", "测试2"]
	}

	def "测试 listByIds 方法"() {
		given: "准备测试数据"
		def ids = [new ObjectId().toHexString(), new ObjectId().toHexString()]

		when: "调用方法"
		repository.listByIds(ids)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 list 方法"() {
		when: "调用无参list方法"
		repository.list()

		then: "验证交互"
		1 * mongoOperations.findAll(TestDocument, _) >> [testDoc]
	}

	def "测试 list(Sort) 方法"() {
		given: "准备测试数据"
		def sort = Sort.by("name")

		when: "调用带排序的list方法"
		repository.list(sort)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 page 方法"() {
		given: "准备测试数据"
		def pageable = PageRequest.of(0, 10)

		when: "调用分页方法"
		repository.page(pageable)

		then: "验证交互"
		1 * mongoOperations.count(_, _) >> 1L
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 insert 方法"() {
		when: "调用插入方法"
		repository.insert(testDoc)

		then: "验证交互"
		1 * mongoOperations.insert(testDoc, _) >> testDoc
	}

	def "测试 save 方法"() {
		when: "调用保存方法"
		repository.save(testDoc)

		then: "验证交互"
		1 * mongoOperations.save(testDoc, _)
	}

	def "测试 updateKeyValueById 方法"() {
		given: "准备测试数据"
		def key = "name"
		def value = "新名称"
		def id = new ObjectId().toHexString()

		when: "调用更新方法"
		repository.updateKeyValueById(key, value, id)

		then: "验证交互"
		1 * mongoOperations.updateFirst(_, _, _) >> Mock(UpdateResult) {
			wasAcknowledged() >> true
			getModifiedCount() >> 1L
		}
	}

	def "测试 removeById 方法"() {
		given: "准备测试数据"
		def id = new ObjectId().toHexString()

		when: "调用删除方法"
		repository.removeById(id)

		then: "验证交互"
		1 * mongoOperations.remove(_, _) >> Mock(DeleteResult) {
			wasAcknowledged() >> true
			getDeletedCount() >> 1L
		}
	}

	def "测试 listByKeyValue 方法"() {
		given: "准备测试数据"
		def key = "name"
		def value = "测试"

		when: "调用方法"
		repository.listByKeyValue(key, value)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 listByKeyValues 方法"() {
		given: "准备测试数据"
		def key = "name"
		def values = ["测试1", "测试2"]

		when: "调用方法"
		repository.listByKeyValues(key, values)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 listByNullValue 方法"() {
		given: "准备测试数据"
		def key = "name"

		when: "调用方法"
		repository.listByNullValue(key)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 listByNotNullValue 方法"() {
		given: "准备测试数据"
		def key = "name"

		when: "调用方法"
		repository.listByNotNullValue(key)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 listByRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "test.*"

		when: "调用方法"
		repository.listByRegex(key, regex)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 listByNotRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "test.*"

		when: "调用方法"
		repository.listByNotRegex(key, regex)

		then: "验证交互"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 stream 方法"() {
		when: "调用方法"
		repository.stream()

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByIds 方法"() {
		given: "准备测试数据"
		def ids = [new ObjectId().toHexString(), new ObjectId().toHexString()]

		when: "调用方法"
		repository.streamByIds(ids)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByKeyValue 方法"() {
		given: "准备测试数据"
		def key = "name"
		def value = "测试"

		when: "调用方法"
		repository.streamByKeyValue(key, value)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByNullValue 方法"() {
		given: "准备测试数据"
		def key = "name"

		when: "调用方法"
		repository.streamByNullValue(key)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByNotNullValue 方法"() {
		given: "准备测试数据"
		def key = "name"

		when: "调用方法"
		repository.streamByNotNullValue(key)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 insertBatch 方法"() {
		given: "准备测试数据"
		def entities = [testDoc, testDoc]

		when: "调用方法"
		repository.insertBatch(entities)

		then: "验证交互"
		1 * mongoOperations.insert(_, _) >> entities
	}

	def "测试 saveBatch 方法"() {
		given: "准备测试数据"
		def entities = [testDoc, testDoc]

		when: "调用方法"
		repository.saveBatch(entities)

		then: "验证交互"
		2 * mongoOperations.save(_, _) >> testDoc
	}

	def "测试 replaceKeyValue 方法"() {
		given: "准备测试数据"
		def key = "name"
		def newValue = "新名称"
		def oldValue = "旧名称"

		when: "调用方法"
		repository.replaceKeyValue(key, newValue, oldValue)

		then: "验证交互"
		1 * mongoOperations.updateMulti(_, _, _) >> Mock(UpdateResult) {
			wasAcknowledged() >> true
			getModifiedCount() >> 1L
		}
	}

	def "测试 remove 方法"() {
		given: "准备测试数据"
		def query = new Query()

		when: "调用方法"
		repository.remove(query)

		then: "验证交互"
		1 * mongoOperations.remove(query, TestDocument, _) >> Mock(DeleteResult) {
			wasAcknowledged() >> true
			getDeletedCount() >> 1L
		}
	}

	def "测试 streamByKeyValues 方法"() {
		given: "准备测试数据"
		def key = "name"
		def values = ["测试1", "测试2"]

		when: "调用方法"
		repository.streamByKeyValues(key, values)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "test.*"

		when: "调用方法"
		repository.streamByRegex(key, regex)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 streamByNotRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "test.*"

		when: "调用方法"
		repository.streamByNotRegex(key, regex)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 removeByIds 方法"() {
		given: "准备测试数据"
		def ids = [new ObjectId().toHexString(), new ObjectId().toHexString()]

		when: "调用方法"
		repository.removeByIds(ids)

		then: "验证交互"
		1 * mongoOperations.remove(_, TestDocument, _) >> Mock(DeleteResult) {
			wasAcknowledged() >> true
			getDeletedCount() >> 2L
		}
	}

	def "测试空值处理场景"() {
		when: "传入空集合"
		def result1 = repository.listByIds(null)
		def result2 = repository.listByIds([])
		def result3 = repository.streamByIds(null)
		def result4 = repository.insertBatch(null)
		def result5 = repository.saveBatch(null)

		then: "验证返回空结果"
		result1 == []
		result2 == []
		result3.count() == 0
		result4 == []
		result5 == []
	}

	def "测试异常场景"() {
		when: "传入null参数"
		repository.getById(null)

		then: "抛出异常"
		thrown(IllegalArgumentException)

		when: "传入空字符串"
		repository.getById("")

		then: "抛出异常"
		thrown(IllegalArgumentException)

		when: "传入null的Query"
		repository.get(null)

		then: "抛出异常"
		thrown(IllegalArgumentException)
	}

	def "测试 list(Query) 方法"() {
		given: "准备测试数据"
		def query = new Query()

		when: "调用方法"
		repository.list(query)

		then: "验证交互"
		1 * mongoOperations.find(query, TestDocument, _) >> [testDoc]
	}

	def "测试 page(Query) 方法"() {
		given: "准备测试数据"
		def query = new Query()
		def pageable = PageRequest.of(0, 10)

		when: "调用方法"
		repository.page(pageable, query)

		then: "验证交互"
		1 * mongoOperations.count(query, _) >> 1L
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 page(Query, Sort) 方法"() {
		given: "准备测试数据"
		def query = new Query()
		def pageable = PageRequest.of(0, 10)
		def sort = Sort.by("name")

		when: "调用方法"
		repository.page(pageable, query, sort)

		then: "验证交互"
		1 * mongoOperations.count(query, _) >> 1L
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]
	}

	def "测试 stream(Query) 方法"() {
		given: "准备测试数据"
		def query = new Query()

		when: "调用方法"
		repository.stream(query)

		then: "验证交互"
		1 * mongoOperations.stream(query, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 stream(Sort) 方法"() {
		given: "准备测试数据"
		def sort = Sort.by("name")

		when: "调用方法"
		repository.stream(sort)

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 stream(Query, Sort) 方法"() {
		given: "准备测试数据"
		def query = new Query()
		def sort = Sort.by("name")

		when: "调用方法"
		repository.stream(query.with(sort))

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}

	def "测试 saveBatch(Collection, boolean) 方法"() {
		given: "准备测试数据"
		def entities = [testDoc, testDoc]

		when: "调用并行处理方法"
		repository.saveBatch(entities, true)

		then: "验证交互"
		2 * mongoOperations.save(_, _) >> testDoc

		when: "调用串行处理方法"
		repository.saveBatch(entities, false)

		then: "验证交互"
		2 * mongoOperations.save(_, _) >> testDoc
	}

	def "测试边界值场景"() {
		when: "传入空字符串key"
		repository.getByKeyValue("", "value")

		then: "抛出异常"
		thrown(IllegalArgumentException)

		when: "传入空白字符串key"
		repository.getByKeyValue("  ", "value")

		then: "抛出异常"
		thrown(IllegalArgumentException)

		when: "传入空集合values"
		repository.listByKeyValues("key", [])

		then: "抛出异常"
		thrown(IllegalArgumentException)

		when: "传入空正则表达式"
		repository.listByRegex("key", "")

		then: "抛出异常"
		thrown(IllegalArgumentException)
	}

	def "测试复杂查询场景"() {
		given: "准备复杂查询条件"
		def query = new Query()
		def sort = Sort.by(Sort.Direction.DESC, "name")
		def pageable = PageRequest.of(0, 10, sort)

		when: "执行分页排序查询"
		repository.page(pageable, query, sort)

		then: "验证交互"
		1 * mongoOperations.count(query, _) >> 100L
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc]

		when: "执行流式查询"
		repository.stream(query.with(sort))

		then: "验证交互"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc].stream()
	}
}
