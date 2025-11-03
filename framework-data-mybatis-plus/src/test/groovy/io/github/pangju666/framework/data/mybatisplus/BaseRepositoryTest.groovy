package io.github.pangju666.framework.data.mybatisplus

import com.google.gson.reflect.TypeToken
import io.github.pangju666.commons.lang.utils.JsonUtils
import io.github.pangju666.framework.data.mybatisplus.entity.TestEntity
import io.github.pangju666.framework.data.mybatisplus.repository.TestRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

import static org.junit.jupiter.api.Assertions.*

@SpringBootTest
public class BaseRepositoryTest {
	@Autowired
	TestRepository repository;

	private static final String TEST1_NAME = "test1";
	private static final String TEST2_NAME = "test2";
	private static final String NON_EXIST_NAME = "test3";

	@Nested
	@DisplayName("JSON操作测试")
	class JsonOperationTests {
		@Test
		@DisplayName("测试JSON对象值查询")
		void testListByJsonObjectValue() {
			// 测试字符串值
			assertEquals(1, repository.listByJsonObjectValue("json_value", "test", "hello world").size());

			// 测试数字值
			assertEquals(1, repository.listByJsonObjectValue("json_value", "age", 1).size());

			// 测试布尔值
			assertEquals(1, repository.listByJsonObjectValue("json_value", "flag", false).size());
			assertTrue(repository.listByJsonObjectValue("json_value", "flag", true).isEmpty());

			// 测试null值
			assertEquals(1, repository.listByJsonObjectValue("json_value", "null_value", null).size());

			// 测试不存在的键
			assertTrue(repository.listByJsonObjectValue("json_value", "abcd", true).isEmpty());

			// 测试嵌套对象
			Map<String, Object> objValue = JsonUtils.fromString("{\"child\": \"test\"}",
				new TypeToken<Map<String, Object>>() {
				});
			assertEquals(1, repository.listByJsonObjectValue("json_value", "obj", objValue).size());

			// 测试数组中的值
			assertEquals(1, repository.listByJsonObjectValue("json_value", "array", "hello world").size());
			assertEquals(1, repository.listByJsonObjectValue("json_value", "array", null).size());

			// 测试嵌套路径
			assertEquals(1, repository.listByJsonObjectValue("json_value", "obj.child", "test").size());
			assertTrue(repository.listByJsonObjectValue("json_value", "obj.child", "test1").isEmpty());
		}

		@Test
		@DisplayName("测试JSON数组值查询")
		void testListByJsonArrayValue() {
			assertFalse(repository.listByJsonArrayValue("json_array", "hello world").isEmpty());
			assertFalse(repository.listByJsonArrayValue("json_array", null).isEmpty());

			Map<String, Object> objValue = JsonUtils.fromString("{\"child\": \"test\"}",
				new TypeToken<Map<String, Object>>() {
				});
			assertFalse(repository.listByJsonArrayValue("json_array", objValue).isEmpty());

			assertTrue(repository.listByJsonArrayValue("json_array", "non-exist-value").isEmpty());
		}

		@Test
		@DisplayName("测试空JSON查询")
		void testEmptyJsonQueries() {
			assertFalse(repository.listByEmptyJsonObject(TestEntity::getJsonValue).isEmpty());
			assertFalse(repository.listByEmptyJsonArray(TestEntity::getJsonArray).isEmpty());
		}
	}

	@Nested
	@DisplayName("基本CRUD操作测试")
	class BasicCrudTests {
		@Test
		@DisplayName("测试列值存在性检查")
		void testExistsQueries() {
			assertTrue(repository.existsByColumnValue(TestEntity::getName, TEST1_NAME));
			assertFalse(repository.existsByColumnValue(TestEntity::getName, NON_EXIST_NAME));

			assertFalse(repository.notExistsByColumnValue(TestEntity::getName, TEST1_NAME));
			assertTrue(repository.notExistsByColumnValue(TestEntity::getName, NON_EXIST_NAME));
		}

		@Test
		@DisplayName("测试单值查询")
		void testSingleValueQueries() {
			assertNotNull(repository.getByColumnValue(TestEntity::getName, TEST2_NAME));
			assertNull(repository.getByColumnValue(TestEntity::getName, NON_EXIST_NAME));
		}

		@Test
		@DisplayName("测试列值列表查询")
		void testColumnValueListQueries() {
			assertEquals(3, repository.listColumnValue(TestEntity::getName).size());
			assertEquals(2, repository.listUniqueColumnValue(TestEntity::getName).size());
		}

		@Test
		@Transactional
		@DisplayName("测试更新操作")
		void testUpdateOperations() {
			// 测试替换值
			assertTrue(repository.replaceColumnValue(TestEntity::getName, TEST2_NAME, TEST1_NAME));
			assertEquals(3, repository.listByColumnValue(TestEntity::getName, TEST2_NAME).size());
		}

		@Test
		@Transactional
		@DisplayName("测试删除操作")
		void testDeleteOperations() {
			// 测试删除单个值
			assertTrue(repository.removeByColumnValue(TestEntity::getName, TEST2_NAME));
			assertEquals(0, repository.listByColumnValue(TestEntity::getName, TEST2_NAME).size());

			// 测试批量删除
			assertTrue(repository.removeByColumnValues(TestEntity::getName,
				Arrays.asList(TEST1_NAME, TEST2_NAME)));
			assertEquals(0, repository.listByColumnValues(TestEntity::getName,
				Arrays.asList(TEST1_NAME, TEST2_NAME)).size());
		}
	}

	@Nested
	@DisplayName("高级查询测试")
	class AdvancedQueryTests {
		@Test
		@DisplayName("测试模糊查询")
		void testLikeQueries() {
			assertEquals(3, repository.listByLikeColumnValue(TestEntity::getName, "test").size());
			assertEquals(2, repository.listByLikeLeftColumnValue(TestEntity::getName, "1").size());
			assertEquals(3, repository.listByLikeRightColumnValue(TestEntity::getName, "test").size());

			assertEquals(0, repository.listByNotLikeColumnValue(TestEntity::getName, "test").size());
			assertEquals(1, repository.listByNotLikeLeftColumnValue(TestEntity::getName, "1").size());
			assertEquals(0, repository.listByNotLikeRightColumnValue(TestEntity::getName, "test").size());
		}

		@Test
		@DisplayName("测试NULL值查询")
		void testNullQueries() {
			assertEquals(1, repository.listByNullColumn(TestEntity::getName).size());
			assertEquals(3, repository.listByNotNullColumn(TestEntity::getName).size());
		}

		@Test
		@DisplayName("测试ID查询")
		void testIdQueries() {
			assertEquals(2, repository.listByIds(Arrays.asList(1L, 2L)).size());
		}
	}

	@Nested
	@DisplayName("边界条件测试")
	class EdgeCaseTests {
		@Test
		@DisplayName("测试特殊字符处理")
		void testSpecialCharacters() {
			// 测试包含特殊字符的名称
			assertFalse(repository.existsByColumnValue(TestEntity::getName, "test%"));
			assertFalse(repository.existsByColumnValue(TestEntity::getName, "test_"));
			assertFalse(repository.existsByColumnValue(TestEntity::getName, "test'"));
		}

		@Test
		@DisplayName("测试空值处理")
		void testEmptyValues() {
			assertTrue(repository.listByColumnValue(TestEntity::getName, "").isEmpty());
			assertTrue(repository.listByColumnValues(TestEntity::getName, List.of()).isEmpty());
			assertTrue(repository.listByLikeColumnValue(TestEntity::getName, "").isEmpty());
		}

		@Test
		@DisplayName("测试大数据量列表")
		void testLargeLists() {
			List<String> largeList = Arrays.asList(new String[1000]);
			assertTrue(repository.listByColumnValues(TestEntity::getName, largeList).isEmpty());
		}
	}

	@Nested
	@DisplayName("模糊删除测试")
	class LikeRemovalTests {
		@Test
		@Transactional
		@DisplayName("测试基本模糊删除")
		void testBasicLikeRemoval() {
			// 基本模糊匹配删除
			assertTrue(repository.removeByLikeColumnValue(TestEntity::getName, "test"));
			assertEquals(0, repository.listByLikeColumnValue(TestEntity::getName, "test").size());
		}

		@Test
		@Transactional
		@DisplayName("测试左模糊删除")
		void testLeftLikeRemoval() {
			// 左模糊匹配删除
			assertTrue(repository.removeByLikeLeftColumnValue(TestEntity::getName, "1"));
			assertEquals(0, repository.listByLikeLeftColumnValue(TestEntity::getName, "1").size());
		}

		@Test
		@Transactional
		@DisplayName("测试右模糊删除")
		void testRightLikeRemoval() {
			// 右模糊匹配删除
			assertTrue(repository.removeByLikeRightColumnValue(TestEntity::getName, "test"));
			assertEquals(0, repository.listByLikeRightColumnValue(TestEntity::getName, "test").size());
		}

		@Test
		@Transactional
		@DisplayName("测试不匹配模糊删除")
		void testNotLikeRemoval() {
			// 不匹配模糊删除
			assertTrue(repository.removeByNotLikeColumnValue(TestEntity::getName, "other"));
			assertEquals(0, repository.listByNotLikeColumnValue(TestEntity::getName, "other").size());
		}

		@Test
		@Transactional
		@DisplayName("测试不匹配左模糊删除")
		void testNotLikeLeftRemoval() {
			// 不匹配左模糊删除
			assertTrue(repository.removeByNotLikeLeftColumnValue(TestEntity::getName, "other"));
			assertEquals(0, repository.listByNotLikeLeftColumnValue(TestEntity::getName, "other").size());
		}

		@Test
		@Transactional
		@DisplayName("测试不匹配右模糊删除")
		void testNotLikeRightRemoval() {
			// 不匹配右模糊删除
			assertTrue(repository.removeByNotLikeRightColumnValue(TestEntity::getName, "other"));
			assertEquals(0, repository.listByNotLikeRightColumnValue(TestEntity::getName, "other").size());
		}

		@Test
		@Transactional
		@DisplayName("测试模糊删除边界条件")
		void testLikeRemovalEdgeCases() {
			// 空字符串
			assertFalse(repository.removeByLikeColumnValue(TestEntity::getName, ""));
			assertFalse(repository.removeByLikeLeftColumnValue(TestEntity::getName, ""));
			assertFalse(repository.removeByLikeRightColumnValue(TestEntity::getName, ""));

			// 特殊字符
			assertTrue(repository.removeByLikeColumnValue(TestEntity::getName, "%"));
			assertFalse(repository.removeByLikeColumnValue(TestEntity::getName, "_"));

			// 不存在的值
			assertFalse(repository.removeByLikeColumnValue(TestEntity::getName, "nonexistent"));
		}
	}
}
