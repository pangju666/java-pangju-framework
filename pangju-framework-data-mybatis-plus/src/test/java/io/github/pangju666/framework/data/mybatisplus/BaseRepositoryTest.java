package io.github.pangju666.framework.data.mybatisplus;

import io.github.pangju666.framework.data.mybatisplus.entity.TestEntity;
import io.github.pangju666.framework.data.mybatisplus.repository.TestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class BaseRepositoryTest {
	@Autowired
	TestRepository repository;

	@Test
	void testListByJsonObjectValue() {
		List<TestEntity> result = repository.listByJsonObjectValue("JSON", "test", "hello world");
		assertFalse(result.isEmpty());
	}

	/*@Test
	void testListByEmptyJsonObject() {
		TestEntity emptyJsonEntity = new TestEntity("空JSON对象");
		emptyJsonEntity.setJsonData("{}");
		repository.save(emptyJsonEntity);

		List<TestEntity> result = repository.listByEmptyJsonObject(TestEntity::getJsonData);
		assertFalse(result.isEmpty());
		assertEquals("空JSON对象", result.get(0).getName());
	}
*/
	@Test
	void testListByJsonArrayValue() {
		List<TestEntity> result = repository.listByJsonArrayValue("json_data", "item1");
		assertFalse(result.isEmpty());
		assertEquals("测试2", result.get(0).getName());
	}

	/*@Test
	void testListByEmptyJsonArray() {
		TestEntity emptyJsonArrayEntity = new TestEntity("空JSON数组");
		emptyJsonArrayEntity.setJsonData("[]");
		repository.save(emptyJsonArrayEntity);

		List<TestEntity> result = repository.listByEmptyJsonArray(TestEntity::getJsonData);
		assertFalse(result.isEmpty());
		assertEquals("空JSON数组", result.get(0).getName());
	}*/

	@Test
	void testExistsById() {
		boolean exists = repository.existsById(1L);
		assertTrue(exists);

		boolean notExists = repository.existsById(999L);
		assertFalse(notExists);
	}

	@Test
	void testNotExistsById() {
		boolean notExists = repository.notExistsById(999L);
		assertTrue(notExists);

		boolean exists = repository.notExistsById(1L);
		assertFalse(exists);
	}

	@Test
	void testExistsByColumnValue() {
		boolean exists = repository.existsByColumnValue(TestEntity::getName, "测试1");
		assertTrue(exists);

		boolean notExists = repository.existsByColumnValue(TestEntity::getName, "不存在");
		assertFalse(notExists);
	}

	@Test
	void testNotExistsByColumnValue() {
		boolean notExists = repository.notExistsByColumnValue(TestEntity::getName, "不存在");
		assertTrue(notExists);

		boolean exists = repository.notExistsByColumnValue(TestEntity::getName, "测试1");
		assertFalse(exists);
	}

	@Test
	void testGetByColumnValue() {
		TestEntity entity = repository.getByColumnValue(TestEntity::getName, "测试1");
		assertNotNull(entity);
		assertEquals(1L, entity.getId());

		TestEntity nullEntity = repository.getByColumnValue(TestEntity::getName, "不存在");
		assertNull(nullEntity);
	}

	@Test
	void testListColumnValue() {
		List<String> names = repository.listColumnValue(TestEntity::getName);
		assertFalse(names.isEmpty());
		assertTrue(names.contains("测试1"));
		assertTrue(names.contains("测试2"));
	}

/*	@Test
	void testListUniqueColumnValue() {
		// 添加一个重复名称的实体
		TestEntity duplicateEntity = new TestEntity("测试1");
		repository.save(duplicateEntity);

		List<String> uniqueNames = repository.listUniqueColumnValue(TestEntity::getName);
		assertFalse(uniqueNames.isEmpty());
		assertEquals(2, uniqueNames.size()); // 只有两个唯一名称
		assertTrue(uniqueNames.contains("测试1"));
		assertTrue(uniqueNames.contains("测试2"));
	}*/

	@Test
	void testListByIds() {
		List<TestEntity> entities = repository.listByIds(Arrays.asList(1L, 2L));
		assertEquals(2, entities.size());

		List<TestEntity> emptyList = repository.listByIds(Collections.emptyList());
		assertTrue(emptyList.isEmpty());

		List<TestEntity> nullList = repository.listByIds(null);
		assertTrue(nullList.isEmpty());
	}

	@Test
	void testListByColumnValue() {
		List<TestEntity> entities = repository.listByColumnValue(TestEntity::getName, "测试1");
		assertFalse(entities.isEmpty());
		assertEquals(1L, entities.get(0).getId());
	}

	@Test
	void testListByColumnValues() {
		List<TestEntity> entities = repository.listByColumnValues(TestEntity::getName, Arrays.asList("测试1", "测试2"));
		assertEquals(2, entities.size());
	}

	@Test
	void testListByNotNullColumn() {
		List<TestEntity> entities = repository.listByNotNullColumn(TestEntity::getName);
		assertFalse(entities.isEmpty());
	}

	@Test
	void testListByNullColumn() {
		TestEntity nullNameEntity = new TestEntity();
		repository.save(nullNameEntity);

		List<TestEntity> entities = repository.listByNullColumn(TestEntity::getName);
		assertFalse(entities.isEmpty());
	}

	@Test
	void testListByLikeColumnValue() {
		List<TestEntity> entities = repository.listByLikeColumnValue(TestEntity::getName, "测试");
		assertEquals(2, entities.size());
	}

	@Test
	void testListByLikeLeftColumnValue() {
		List<TestEntity> entities = repository.listByLikeLeftColumnValue(TestEntity::getName, "试1");
		assertEquals(1, entities.size());
		assertEquals("测试1", entities.get(0).getName());
	}

	@Test
	void testListByLikeRightColumnValue() {
		List<TestEntity> entities = repository.listByLikeRightColumnValue(TestEntity::getName, "测试");
		assertEquals(2, entities.size());
	}

/*	@Test
	void testListByNotLikeColumnValue() {
		TestEntity otherEntity = new TestEntity("其他");
		repository.save(otherEntity);

		List<TestEntity> entities = repository.listByNotLikeColumnValue(TestEntity::getName, "测试");
		assertEquals(1, entities.size());
		assertEquals("其他", entities.get(0).getName());
	}

	@Test
	void testSaveBatch() {
		List<TestEntity> entities = Arrays.asList(
			new TestEntity("批量1"),
			new TestEntity("批量2")
		);
		boolean result = repository.saveBatch(entities);
		assertTrue(result);

		List<TestEntity> saved = repository.listByLikeColumnValue(TestEntity::getName, "批量");
		assertEquals(2, saved.size());
	}*/

/*	@Test
	void testUpdateBatchById() {
		entity1.setName("更新1");
		entity2.setName("更新2");

		boolean result = repository.updateBatchById(Arrays.asList(entity1, entity2));
		assertTrue(result);

		TestEntity updated1 = repository.getById(1L);
		assertEquals("更新1", updated1.getName());

		TestEntity updated2 = repository.getById(2L);
		assertEquals("更新2", updated2.getName());
	}*/

	@Test
	void testRemoveByColumnValue() {
		boolean result = repository.removeByColumnValue(TestEntity::getName, "测试1");
		assertTrue(result);

		TestEntity removed = repository.getById(1L);
		assertNull(removed);
	}

	@Test
	void testRemoveByColumnValues() {
		boolean result = repository.removeByColumnValues(TestEntity::getName, Arrays.asList("测试1", "测试2"));
		assertTrue(result);

		List<TestEntity> all = repository.list();
		assertTrue(all.isEmpty());
	}

	@Test
	void testRemoveByLikeColumnValue() {
		boolean result = repository.removeByLikeColumnValue(TestEntity::getName, "测试");
		assertTrue(result);

		List<TestEntity> all = repository.list();
		assertTrue(all.isEmpty());
	}
}
