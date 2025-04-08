package io.github.pangju666.framework.data.mybatisplus;

import com.google.gson.reflect.TypeToken;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.data.mybatisplus.entity.MySQLTestEntity;
import io.github.pangju666.framework.data.mybatisplus.repository.MySQLTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("mysql")
@SpringBootTest
public class MysqlBaseRepositoryTest {
	@Autowired
	MySQLTestRepository repository;

	@Test
	void testListByJsonObjectValue() {
		List<MySQLTestEntity> result1 = repository.listByJsonObjectValue("json_value", "test",
			"hello world");
		assertEquals(1, result1.size());

		List<MySQLTestEntity> result2 = repository.listByJsonObjectValue("json_value", "age",
			1);
		assertEquals(1, result2.size());

		List<MySQLTestEntity> result3 = repository.listByJsonObjectValue("json_value", "flag",
			false);
		assertEquals(1, result3.size());

		List<MySQLTestEntity> result4 = repository.listByJsonObjectValue("json_value", "null_value",
			null);
		assertEquals(1, result4.size());

		List<MySQLTestEntity> result5 = repository.listByJsonObjectValue("json_value", "flag",
			true);
		assertTrue(result5.isEmpty());

		List<MySQLTestEntity> result6 = repository.listByJsonObjectValue("json_value", "abcd",
			true);
		assertTrue(result6.isEmpty());

		List<MySQLTestEntity> result7 = repository.listByJsonObjectValue("json_value", "obj",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertEquals(1, result7.size());

		List<MySQLTestEntity> result8 = repository.listByJsonObjectValue("json_value", "array",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertEquals(1, result8.size());

		List<MySQLTestEntity> result9 = repository.listByJsonObjectValue("json_value", "array",
			"hello world");
		assertEquals(1, result9.size());

		List<MySQLTestEntity> result10 = repository.listByJsonObjectValue("json_value", "array",
			null);
		assertEquals(1, result10.size());

		List<MySQLTestEntity> result11 = repository.listByJsonObjectValue("json_value", "obj.child",
			"test");
		assertEquals(1, result11.size());

		List<MySQLTestEntity> result12 = repository.listByJsonObjectValue("json_value", "obj.child",
			"test1");
		assertTrue(result12.isEmpty());
	}

	@Test
	void testListByJsonArrayValue() {
		List<MySQLTestEntity> result1 = repository.listByJsonArrayValue("json_array", "hello world");
		assertFalse(result1.isEmpty());

		List<MySQLTestEntity> result2 = repository.listByJsonArrayValue("json_array", null);
		assertFalse(result2.isEmpty());

		List<MySQLTestEntity> result3 = repository.listByJsonArrayValue("json_array",
			JsonUtils.fromString("{\"child\": \"test\"}", new TypeToken<Map<String, Object>>() {
			}));
		assertFalse(result3.isEmpty());

		List<MySQLTestEntity> result4 = repository.listByJsonArrayValue("json_array", "aaaaahello world");
		assertTrue(result4.isEmpty());
	}
}
