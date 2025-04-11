package io.github.pangju666.framework.data.mongodb

import io.github.pangju666.framework.data.mongodb.model.TestDocument
import io.github.pangju666.framework.data.mongodb.repository.TestRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class BaseRepositorySpec extends Specification {
	@Autowired
	MongoOperations mongoOperations
	@Autowired
	TestRepository repository

	def "写入初始数据"() {
		setup:
		// 初始化测试数据
		TestDocument testDoc1 = new TestDocument(
			name: "测试文档1",
			value: 100
		)
		mongoOperations.insert(testDoc1)
		TestDocument testDoc2 = new TestDocument(
			name: "测试文档2",
			value: 200
		)
		mongoOperations.insert(testDoc2)
	}

	def "测试基础查询操作 - 按ID查询"() {
		given: "准备测试数据"
		def id = testDoc1.id

		when: "执行查询"
		repository.getById(id)

		then: "验证查询调用"
		1 * mongoOperations.findById(id, TestDocument, _) >> testDoc1
	}

	def "测试基础查询操作 - 按ObjectId查询"() {
		given: "准备测试数据"
		def objectId = new ObjectId()

		when: "执行查询"
		repository.getByObjectId(objectId)

		then: "验证查询调用"
		1 * mongoOperations.findById(objectId, TestDocument, _) >> testDoc1
	}

	@Unroll
	def "测试字段值查询 - key=#key, value=#value"() {
		when: "执行查询"
		repository.getByKeyValue(key, value)

		then: "验证查询调用"
		1 * mongoOperations.findOne(_, TestDocument, _) >> result

		where:
		key     | value  | result
		"name"  | "测试" | testDoc1
		"value" | 100    | testDoc1
		"name"  | null   | null
	}

	def "测试列表查询操作"() {
		when: "查询所有文档"
		repository.list()

		then: "验证查询调用"
		1 * mongoOperations.findAll(TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试排序查询操作"() {
		given: "准备排序条件"
		def sort = Sort.by("name").ascending()

		when: "执行排序查询"
		repository.list(sort)

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试分页查询操作"() {
		given: "准备分页参数"
		def pageable = PageRequest.of(0, 10)

		when: "执行分页查询"
		def result = repository.page(pageable)

		then: "验证查询调用"
		1 * mongoOperations.count(_, _) >> 2
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
		result.totalElements == 2
		result.content.size() == 2
	}

	def "测试正则表达式查询"() {
		given: "准备正则表达式"
		def pattern = ~/test.*/

		when: "执行正则查询"
		repository.listByRegex("name", pattern)

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试流式查询操作"() {
		when: "执行流式查询"
		repository.stream()

		then: "验证查询调用"
		1 * mongoOperations.stream(_, TestDocument, _) >> [testDoc1, testDoc2].stream()
	}

	def "测试存在性检查"() {
		when: "检查文档是否存在"
		repository.existsById(testDoc1.id)

		then: "验证检查调用"
		1 * mongoOperations.exists(_, TestDocument, _) >> true
	}

	def "测试空值查询"() {
		when: "查询null值"
		repository.listByNullValue("name")

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> []
	}

	def "测试非空值查询"() {
		when: "查询非null值"
		repository.listByNotNullValue("name")

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试自定义查询条件"() {
		given: "准备查询条件"
		def query = new Query()

		when: "执行自定义查询"
		repository.list(query)

		then: "验证查询调用"
		1 * mongoOperations.find(query, TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试批量ID查询"() {
		given: "准备ID列表"
		def ids = [testDoc1.id, testDoc2.id]

		when: "执行批量查询"
		repository.listByIds(ids)

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
	}

	def "测试批量ObjectId查询"() {
		given: "准备ObjectId列表"
		def objectIds = [new ObjectId(), new ObjectId()]

		when: "执行批量查询"
		repository.listByObjectIds(objectIds)

		then: "验证查询调用"
		1 * mongoOperations.find(_, TestDocument, _) >> [testDoc1, testDoc2]
	}
}
