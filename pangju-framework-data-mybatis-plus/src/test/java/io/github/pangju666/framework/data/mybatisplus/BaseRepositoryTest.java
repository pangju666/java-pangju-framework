package io.github.pangju666.framework.data.mybatisplus;

import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.data.mybatisplus.entity.TestEntity;
import io.github.pangju666.framework.data.mybatisplus.repository.TestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BaseRepositoryTest {
	@Autowired
	TestRepository repository;

	@Test
	public void cleanData() {
		repository.removeByColumnValues(TestEntity::getName, Arrays.asList("test1", "test2"));
	}

	@Test
	public void testListByJsonObjectValue() {
		List<TestEntity> result1 = repository.listByJsonObjectValue("json_value", "test",
			"hello world");
		assertEquals(1, result1.size());

		List<TestEntity> result2 = repository.listByJsonObjectValue("json_value", "age",
			1);
		assertEquals(1, result2.size());

		List<TestEntity> result3 = repository.listByJsonObjectValue("json_value", "flag",
			false);
		assertEquals(1, result3.size());

		List<TestEntity> result4 = repository.listByJsonObjectValue("json_value", "null_value",
			null);
		assertEquals(1, result4.size());

		List<TestEntity> result5 = repository.listByJsonObjectValue("json_value", "flag",
			true);
		assertTrue(result5.isEmpty());

		List<TestEntity> result6 = repository.listByJsonObjectValue("json_value", "abcd",
			true);
		assertTrue(result6.isEmpty());

		List<TestEntity> result7 = repository.listByJsonObjectValue("json_value", "obj",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertEquals(1, result7.size());

		List<TestEntity> result8 = repository.listByJsonObjectValue("json_value", "array",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertEquals(1, result8.size());

		List<TestEntity> result9 = repository.listByJsonObjectValue("json_value", "array",
			"hello world");
		assertEquals(1, result9.size());

		List<TestEntity> result10 = repository.listByJsonObjectValue("json_value", "array",
			null);
		assertEquals(1, result10.size());

		List<TestEntity> result11 = repository.listByJsonObjectValue("json_value", "obj.child",
			"test");
		assertEquals(1, result11.size());

		List<TestEntity> result12 = repository.listByJsonObjectValue("json_value", "obj.child",
			"test1");
		assertTrue(result12.isEmpty());
	}

	@Test
	public void testListByJsonArrayValue() {
		List<TestEntity> result1 = repository.listByJsonArrayValue("json_array", "hello world");
		assertFalse(result1.isEmpty());

		List<TestEntity> result2 = repository.listByJsonArrayValue("json_array", null);
		assertFalse(result2.isEmpty());

		List<TestEntity> result3 = repository.listByJsonArrayValue("json_array",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertFalse(result3.isEmpty());

		List<TestEntity> result4 = repository.listByJsonArrayValue("json_array", "aaaaahello world");
		assertTrue(result4.isEmpty());
	}

	@Test
	public void listByEmptyJsonObject() {
		var list = repository.listByEmptyJsonObject(TestEntity::getJsonValue);
		assertFalse(list.isEmpty());
	}

	@Test
	public void listByEmptyJsonArray() {
		var list = repository.listByEmptyJsonArray(TestEntity::getJsonArray);
		assertFalse(list.isEmpty());
	}

	@Test
	public void existsByColumnValue() {
		assertTrue(repository.existsByColumnValue(TestEntity::getName, "test1"));
		assertFalse(repository.existsByColumnValue(TestEntity::getName, "test3"));
	}

	@Test
	public void notExistsByColumnValue() {
		assertFalse(repository.notExistsByColumnValue(TestEntity::getName, "test1"));
		assertTrue(repository.notExistsByColumnValue(TestEntity::getName, "test3"));
	}

	@Test
	public void getByColumnValue() {
		assertNotNull(repository.getByColumnValue(TestEntity::getName, "test2"));
		assertNull(repository.getByColumnValue(TestEntity::getName, "test3"));
	}

	@Test
	public void listColumnValue() {
		assertEquals(3, repository.listColumnValue(TestEntity::getName).size());
	}

	@Test
	public void listUniqueColumnValue() {
		assertEquals(2, repository.listUniqueColumnValue(TestEntity::getName).size());
	}

	@Test
	public void listByColumnValue() {
		assertEquals(2, repository.listByColumnValue(TestEntity::getName, "test1").size());
		assertEquals(1, repository.listByColumnValue(TestEntity::getName, "test2").size());
	}

	@Test
	public void listByColumnValues() {
		assertEquals(3, repository.listByColumnValues(TestEntity::getName, Arrays.asList("test1", "test2")).size());
	}

	@Test
	public void listByNotNullColumn() {
		assertEquals(3, repository.listByNotNullColumn(TestEntity::getName).size());
	}

	@Test
	public void listByNullColumn() {
		assertEquals(1, repository.listByNullColumn(TestEntity::getName).size());
	}

	@Test
	public void listByLikeColumnValue() {
		assertEquals(3, repository.listByLikeColumnValue(TestEntity::getName, "est").size());
	}

	@Test
	public void listByLikeLeftColumnValue() {
		assertEquals(2, repository.listByLikeColumnValue(TestEntity::getName, "est1").size());
	}

	@Test
	public void listByLikeRightColumnValue() {
		assertEquals(3, repository.listByLikeColumnValue(TestEntity::getName, "test").size());
	}

	@Test
	public void listByNotLikeColumnValue() {
		assertEquals(0, repository.listByNotLikeColumnValue(TestEntity::getName, "est").size());
	}

	@Test
	public void listByNotLikeLeftColumnValue() {
		assertEquals(1, repository.listByNotLikeLeftColumnValue(TestEntity::getName, "est1").size());
	}

	@Test
	public void listByNotLikeRightColumnValue() {
		assertEquals(0, repository.listByNotLikeRightColumnValue(TestEntity::getName, "test").size());
	}

	@Test
	public void listByIds() {
		assertEquals(2, repository.listByIds(Arrays.asList(1L, 2L)).size());
	}

	@Transactional
	@Test
	public void replaceColumnValue() {
		assertEquals(2, repository.listByColumnValue(TestEntity::getName, "test1").size());
		assertTrue(repository.replaceColumnValue(TestEntity::getName, "test2", "test1"));
		assertEquals(3, repository.listByColumnValue(TestEntity::getName, "test2").size());
	}

	@Test
	@Transactional
	public void removeByColumnValue() {
		assertEquals(2, repository.listByColumnValue(TestEntity::getName, "test1").size());
		assertTrue(repository.removeByColumnValue(TestEntity::getName, "test1"));
		assertEquals(0, repository.listByColumnValue(TestEntity::getName, "test1").size());
	}

	@Test
	@Transactional
	public void removeByColumnValues() {
		assertEquals(3, repository.listByColumnValues(TestEntity::getName, Arrays.asList("test1", "test2")).size());
		assertTrue(repository.removeByColumnValues(TestEntity::getName, Arrays.asList("test1", "test2")));
		assertEquals(0, repository.listByColumnValues(TestEntity::getName, Arrays.asList("test1", "test2")).size());
	}
}
