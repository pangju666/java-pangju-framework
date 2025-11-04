package io.github.pangju666.framework.data.mongodb

import io.github.pangju666.framework.data.mongodb.model.TestDocument
import io.github.pangju666.framework.data.mongodb.model.document.BaseDocument
import io.github.pangju666.framework.data.mongodb.repository.TestRepository
import io.github.pangju666.framework.data.mongodb.utils.QueryUtils
import org.apache.commons.collections4.CollectionUtils
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoOperations
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
	TestDocument savedDoc

	def setup() {
		testDoc = new TestDocument(
			id: new ObjectId().toHexString(),
			name: "测试文档",
			value: "测试值"
		)
		savedDoc = repository.insert(testDoc)
	}

	def cleanup() {
		repository.removeById(savedDoc.id)
	}

	def "测试基本的CRUD操作"() {
		expect: "文档应该已经保存"
		repository.existsById(savedDoc.id)

		when: "更新文档"
		savedDoc.name = "更新的文档"
		repository.save(savedDoc)
		def updatedDoc = repository.getById(savedDoc.id)

		then: "文档应该已更新"
		updatedDoc.name == "更新的文档"

		when: "删除文档"
		repository.removeById(savedDoc.id)

		then: "文档应该已删除"
		!repository.existsById(savedDoc.id)
	}

	def "测试查询操作"() {
		when: "按字段值查询"
		def result = repository.getByKeyValue("name", testDoc.name)

		then: "应该能找到文档"
		result.id == savedDoc.id

		when: "使用正则表达式查询"
		def regexResults = repository.listByRegex("name", "测试.*")

		then: "应该能找到文档"
		!regexResults.empty
		regexResults[0].id == savedDoc.id
	}

	def "测试批量操作"() {
		given: "准备多个测试文档"
		def docs = [
			new TestDocument(id: new ObjectId().toHexString(), name: "批量测试1"),
			new TestDocument(id: new ObjectId().toHexString(), name: "批量测试2")
		]

		when: "批量插入"
		def insertedDocs = repository.insertBatch(docs)

		then: "应该成功插入所有文档"
		insertedDocs.size() == 2

		when: "批量查询"
		def foundDocs = repository.listByIds(insertedDocs*.id)

		then: "应该能找到所有文档"
		foundDocs.size() == 2

		cleanup:
		repository.removeByIds(insertedDocs*.id)
	}

	def "测试分页和排序"() {
		given: "准备多个测试文档"
		def docs = (1..5).collect {
			return new TestDocument(
				id: new ObjectId().toHexString(),
				name: "分页测试${it}",
				value: "值${it}"
			)
		}
		repository.insertBatch(docs)

		when: "执行分页查询"
		def pageable = PageRequest.of(0, 3)
		def page = repository.page(pageable)

		then: "应该返回正确的分页结果"
		page.totalElements > 0
		page.content.size() <= 3

		when: "执行排序查询"
		def sort = Sort.by(Sort.Direction.DESC, "name")
		def sortedList = repository.list(QueryUtils.queryByIds(BaseDocument.getIdList(docs)).with(sort))

		then: "结果应该已排序"
		sortedList.size() > 0
		CollectionUtils.isEqualCollection(sortedList, docs)

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试流式查询"() {
		given: "准备多个测试文档"
		def docs = (1..3).collect {
			new TestDocument(
				id: new ObjectId().toHexString(),
				name: "流式测试${it}"
			)
		}
		repository.insertBatch(docs)

		when: "执行流式查询"
		def stream = repository.stream()
		def results = stream.toList()

		then: "应该能获取所有文档"
		results.size() >= docs.size()

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试特殊值查询"() {
		given: "准备含特殊值的文档"
		def nullDoc = new TestDocument(
			id: new ObjectId().toHexString(),
			name: null
		)
		repository.insert(nullDoc)

		when: "查询null值"
		def nullResults = repository.listByNullValue("name")

		then: "应该能找到null值文档"
		nullResults.find { it.id == nullDoc.id }

		when: "查询非null值"
		def notNullResults = repository.listByNotNullValue("name")

		then: "应该能找到非null值文档"
		notNullResults.find { it.id == savedDoc.id }

		cleanup:
		repository.removeById(nullDoc.id)
	}

	def "测试复杂查询场景"() {
		when: "组合查询条件"
		def query = new Query()
		def sort = Sort.by("name")
		def pageable = PageRequest.of(0, 10)
		def result = repository.page(pageable, query, sort)

		then: "应该返回正确的结果"
		result.totalElements > 0
		result.content.size() > 0

		when: "使用distinct查询"
		def distinctValues = repository.listDistinctKeyValues("name", String)

		then: "应该返回不重复的值"
		distinctValues.size() > 0
		distinctValues.unique() == distinctValues
	}

	def "测试边界条件"() {
		when: "查询不存在的ID"
		def nonExistentId = new ObjectId().toHexString()
		def result = repository.getById(nonExistentId)

		then: "应该返回null"
		result == null

		when: "更新不存在的文档"
		def updated = repository.updateKeyValueById("name", "新值", nonExistentId)

		then: "应该返回false"
		!updated
	}

	def "测试字段值替换操作"() {
		given: "准备多个相同字段值的文档"
		def docs = (1..3).collect {
			new TestDocument(
				id: new ObjectId().toHexString(),
				name: "旧值",
				value: "测试${it}"
			)
		}
		repository.insertBatch(docs)

		when: "批量替换字段值"
		def count = repository.replaceKeyValue("name", "新值", "旧值")

		then: "应该成功更新所有文档"
		count == 3
		repository.listByKeyValue("name", "新值").size() == 3

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试批量保存的并行处理"() {
		given: "准备大量测试文档"
		def docs = (1..10).collect {
			new TestDocument(
				id: new ObjectId().toHexString(),
				name: "并行测试${it}",
				value: "值${it}"
			)
		}

		when: "使用并行方式批量保存"
		def savedDocs = repository.saveBatch(docs, true)

		then: "所有文档应该被保存"
		savedDocs.size() == 10
		repository.count() >= 10

		cleanup:
		repository.removeByIds(savedDocs*.id)
	}

	def "测试ObjectId相关操作"() {
		given: "准备ObjectId文档"
		def objectId = new ObjectId().toHexString()
		def objIdDoc = new TestDocument(
			id: objectId,
			name: "ObjectId测试"
		)
		repository.insert(objIdDoc)

		when: "使用ObjectId查询"
		def exists = repository.existsById(objectId)
		def found = repository.getById(objectId)

		then: "应该能找到文档"
		exists
		found != null
		found.id == objectId

		when: "使用ObjectId更新"
		def updated = repository.updateKeyValueById("name", "更新的ObjectId测试", objectId)

		then: "更新应该成功"
		updated
		repository.getById(objectId).name == "更新的ObjectId测试"

		when: "使用ObjectId删除"
		def removed = repository.removeById(objectId)

		then: "删除应该成功"
		removed
		!repository.existsById(objectId)
	}

	def "测试集合统计操作"() {
		given: "准备多个测试文档"
		def docs = (1..5).collect {
			new TestDocument(
				id: new ObjectId().toHexString(),
				name: "统计测试",
				value: "值${it}"
			)
		}
		repository.insertBatch(docs)

		when: "执行计数操作"
		def totalCount = repository.count()
		def queryCount = repository.count(new Query())

		then: "应该返回正确的数量"
		totalCount >= 5
		queryCount >= 5

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试复合条件查询"() {
		given: "准备不同类型的测试文档"
		def docs = [
			new TestDocument(id: new ObjectId().toHexString(), name: "测试A", value: "1"),
			new TestDocument(id: new ObjectId().toHexString(), name: "测试B", value: "2"),
			new TestDocument(id: new ObjectId().toHexString(), name: "测试A", value: "3")
		]
		repository.insertBatch(docs)

		when: "执行多字段查询"
		def results = repository.listByKeyValues("name", ["测试A", "测试B"])

		then: "应该返回匹配的文档"
		results.size() == 3

		when: "执行正则和排序组合查询"
		def sortedResults = repository.list(
			QueryUtils.queryByIds(BaseDocument.getIdList(docs)).with(Sort.by(Sort.Direction.DESC, "value"))
		)

		then: "结果应该已排序"
		CollectionUtils.isEqualCollection(sortedResults, docs.sort { it.value }.reverse())

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试异常处理场景"() {
		when: "使用null参数"
		repository.getByKeyValue(null, "value")

		then: "应该抛出异常"
		thrown(IllegalArgumentException)

		when: "使用空字符串ID"
		repository.getById(null)

		then: "应该抛出异常"
		thrown(IllegalArgumentException)

		when: "使用null的Query对象"
		repository.list(null as Query)

		then: "应该抛出异常"
		thrown(IllegalArgumentException)
	}
	
	def "测试流式查询的排序和过滤"() {
		given: "准备测试数据"
		def docs = (1..5).collect {
			new TestDocument(
				id: new ObjectId().toHexString(),
				name: "流式测试${it}",
				value: "${it}"
			)
		}
		repository.insertBatch(docs)

		when: "执行带排序的流式查询"
		def sortedStream = repository.stream(QueryUtils.queryByIds(BaseDocument.getIdList(docs))
			.with(Sort.by("value")))
		def sortedResults = sortedStream.toList()

		then: "结果应该已排序"
		CollectionUtils.isEqualCollection(sortedResults, docs.sort { it.value })

		when: "执行带查询条件的流式查询"
		def query = new Query()
		def queryStream = repository.stream(query)
		def queryResults = queryStream.toList()

		then: "应该返回匹配的结果"
		queryResults.size() >= 5

		cleanup:
		repository.removeByIds(docs*.id)
	}

	def "测试空值集合处理"() {
		when: "传入null的集合"
		def nullResults = repository.insertBatch(null)
		def nullSaveResults = repository.saveBatch(null)
		def nullStreamResults = repository.streamByIds(null)

		then: "应该返回空结果"
		nullResults.empty
		nullSaveResults.empty
		nullStreamResults.count() == 0

		when: "传入空集合"
		def emptyResults = repository.insertBatch([])
		def emptySaveResults = repository.saveBatch([])
		def emptyStreamResults = repository.streamByIds([])

		then: "应该返回空结果"
		emptyResults.empty
		emptySaveResults.empty
		emptyStreamResults.count() == 0
	}

	def "测试 list(Query, Sort) 方法"() {
		given: "准备测试数据"
		def query = new Query()
		def sort = Sort.by("name")

		when: "执行带查询条件和排序的查询"
		def results = repository.list(query.with(sort))

		then: "应该返回排序后的结果"
		results.size() >= 1
		results == results.sort { it.name }
	}

	def "测试 streamByKeyValue 方法"() {
		given: "准备测试数据"
		def key = "name"
		def value = "测试文档"

		when: "执行流式查询"
		def stream = repository.streamByKeyValue(key, value)
		def results = stream.toList()

		then: "应该返回匹配的结果"
		results.find { it.id == savedDoc.id }
	}

	def "测试 streamByNotRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "其他.*"

		when: "执行不匹配正则的流式查询"
		def stream = repository.streamByNotRegex(key, regex)
		def results = stream.toList()

		then: "应该返回不匹配正则的结果"
		results.find { it.id == savedDoc.id }
	}

	def "测试 existsById 方法"() {
		expect: "存在的ID应该返回true"
		repository.existsById(savedDoc.id)

		and: "不存在的ID应该返回false"
		!repository.existsById(new ObjectId().toHexString())
	}

	def "测试 listByNotRegex 方法"() {
		given: "准备测试数据"
		def key = "name"
		def regex = "其他.*"

		when: "执行不匹配正则的查询"
		def results = repository.listByNotRegex(key, regex)

		then: "应该返回不匹配正则的结果"
		results.find { it.id == savedDoc.id }
	}

	def "测试参数验证"() {
		when: "使用空白key进行正则查询"
		repository.listByRegex(" ", "test")

		then: "应该抛出异常"
		thrown(IllegalArgumentException)

		when: "使用空白regex进行正则查询"
		repository.listByRegex("name", " ")

		then: "应该抛出异常"
		thrown(IllegalArgumentException)

		when: "使用null的ObjectId"
		repository.existsById(null)

		then: "应该抛出异常"
		thrown(IllegalArgumentException)
	}
}
