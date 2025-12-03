package io.github.pangju666.framework.data.mybatisplus.test;

import io.github.pangju666.framework.data.mybatisplus.TestApplication;
import io.github.pangju666.framework.data.mybatisplus.entity.DocDO;
import io.github.pangju666.framework.data.mybatisplus.entity.UserDO;
import io.github.pangju666.framework.data.mybatisplus.repository.DocRepository;
import io.github.pangju666.framework.data.mybatisplus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
class BaseRepositoryMySqlTests {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DocRepository docRepo;

	// -------- 基础存在性与查询 --------

	@Test
	void existsById_and_notExistsById() {
		assertTrue(userRepo.existsById(1L));
		assertFalse(userRepo.notExistsById(1L));
		assertFalse(userRepo.existsById(999L));
		assertTrue(userRepo.notExistsById(999L));
	}

	@Test
	void getByColumnValue_and_getOptByColumnValue() {
		UserDO u1 = userRepo.getByColumnValue(UserDO::getName, "Carol");
		UserDO u2 = userRepo.getByColumnValue(UserDO::getEmail, null);
		Optional<UserDO> opt1 = userRepo.getOptByColumnValue(UserDO::getEmail, "carol@example.com");
		Optional<UserDO> opt2 = userRepo.getOptByColumnValue(UserDO::getEmail, "nope@example.com");

		assertNotNull(u1);
		assertEquals("Carol", u1.getName());

		assertNotNull(u2);
		assertEquals("Bob", u2.getName());

		assertTrue(opt1.isPresent());
		assertEquals("Carol", opt1.get().getName());

		assertFalse(opt2.isPresent());
	}

	@Test
	void listByColumnValue_multiple_results() {
		List<UserDO> usersByAge25 = userRepo.listByColumnValue(UserDO::getAge, 25);
		Set<String> names = Set.copyOf(usersByAge25.stream().map(UserDO::getName).toList());
		assertEquals(Set.of("Bob", "Carol"), names);
		assertTrue(usersByAge25.stream().allMatch(u -> u.getAge() == 25));
	}

	@Test
	void listColumnValue_allows_duplicates() {
		List<String> names = userRepo.listColumnValue(UserDO::getName);
		assertTrue(names.containsAll(List.of("Alice", "Bob", "Carol")));
		long countAlice = names.stream().filter("Alice"::equals).count();
		assertEquals(2, countAlice);
	}

	@Test
	void listUniqueColumnValue_distinct_values() {
		List<String> uniqueNames = userRepo.listUniqueColumnValue(UserDO::getName);
		Set<String> set = Set.copyOf(uniqueNames);
		assertEquals(Set.of("Alice", "Bob", "Carol"), set);
	}

	// -------- 批量查询与 Supplier 重载 --------

	@Test
	void listByIds_default_and_explicit_batch_size() {
		List<UserDO> listDefault = userRepo.listByIds(Arrays.asList(1L, 2L, 3L, 4L));
		Set<Long> idsDefault = Set.copyOf(listDefault.stream().map(UserDO::getId).toList());
		assertEquals(Set.of(1L, 2L, 3L, 4L), idsDefault);

		List<UserDO> listBatch = userRepo.listByIds(Arrays.asList(1L, 2L, 3L, 4L), 2);
		Set<Long> idsBatch = Set.copyOf(listBatch.stream().map(UserDO::getId).toList());
		assertEquals(Set.of(1L, 2L, 3L, 4L), idsBatch);
	}

	@Test
	void listByColumnValues_default_and_custom_batch_size() {
		List<Integer> ages = List.of(25, 30, 28);
		List<UserDO> listDefault = userRepo.listByColumnValues(UserDO::getAge, ages);
		List<UserDO> listBatch = userRepo.listByColumnValues(UserDO::getAge, ages, 2);

		Set<String> namesDefault = Set.copyOf(listDefault.stream().map(UserDO::getName).toList());
		Set<String> namesBatch = Set.copyOf(listBatch.stream().map(UserDO::getName).toList());
		assertEquals(Set.of("Alice", "Bob", "Carol"), namesDefault);
		assertEquals(Set.of("Alice", "Bob", "Carol"), namesBatch);
	}

	@Test
	void listByColumnValues_with_Supplier_batch_overloads() {
		List<Integer> ages = List.of(25, 30, 28);

		var listSupplierDefaultBatch = userRepo.listByColumnValues(
			UserDO::getAge, ages, () -> userRepo.lambdaQuery().ge(UserDO::getAge, 25)
		);
		assertEquals(Set.of("Alice", "Bob", "Carol"),
			Set.copyOf(listSupplierDefaultBatch.stream().map(UserDO::getName).toList()));

		var listSupplierExplicitBatch = userRepo.listByColumnValues(
			UserDO::getAge, ages, 2, () -> userRepo.lambdaQuery().ge(UserDO::getAge, 25)
		);
		assertEquals(Set.of("Alice", "Bob", "Carol"),
			Set.copyOf(listSupplierExplicitBatch.stream().map(UserDO::getName).toList()));
	}

	// -------- 列值存在性与 null 判断 --------

	@Test
	void existsByColumnValue_null_and_non_null() {
		assertTrue(userRepo.existsByColumnValue(UserDO::getEmail, null));
		assertTrue(userRepo.existsByColumnValue(UserDO::getEmail, "alice@example.com"));
	}

	@Test
	void notExistsByColumnValue_semantics() {
		assertTrue(userRepo.notExistsByColumnValue(UserDO::getEmail, null));
		assertFalse(userRepo.notExistsByColumnValue(UserDO::getEmail, "alice@example.com"));
		assertTrue(userRepo.notExistsByColumnValue(UserDO::getEmail, "nope@example.com"));
	}

	@Test
	void listByColumnNull_and_NotNull() {
		List<UserDO> nullEmails = userRepo.listByColumnNull(UserDO::getEmail);
		List<UserDO> notNullEmails = userRepo.listByColumnNotNull(UserDO::getEmail);

		assertTrue(nullEmails.stream().allMatch(u -> u.getEmail() == null));
		assertTrue(notNullEmails.stream().allMatch(u -> u.getEmail() != null));
		assertTrue(nullEmails.stream().map(UserDO::getName).toList().contains("Bob"));
	}

	// -------- LIKE / NOT LIKE 变体 --------

	@Test
	void like_and_notLike_variants() {
		var likeAli = userRepo.listByColumnLikeColumn(UserDO::getName, "Ali");
		assertTrue(likeAli.stream().map(UserDO::getName).toList().contains("Alice"));

		var likeLeftCe = userRepo.listByColumnLikeLeft(UserDO::getName, "ce");
		assertTrue(likeLeftCe.stream().map(UserDO::getName).toList().contains("Alice"));

		var likeRightAl = userRepo.listByColumnLikeRight(UserDO::getName, "Al");
		assertTrue(likeRightAl.stream().map(UserDO::getName).toList().contains("Alice"));

		var notLikeLi = userRepo.listByColumnNotLike(UserDO::getName, "li");
		assertEquals(Set.of("Bob", "Carol"), Set.copyOf(notLikeLi.stream().map(UserDO::getName).toList()));

		var notLikeLeftCe = userRepo.listByColumnNotLikeLeft(UserDO::getName, "ce");
		assertEquals(Set.of("Bob", "Carol"), Set.copyOf(notLikeLeftCe.stream().map(UserDO::getName).toList()));

		var notLikeRightAl = userRepo.listByColumnNotLikeRight(UserDO::getName, "Al");
		assertEquals(Set.of("Bob", "Carol"), Set.copyOf(notLikeRightAl.stream().map(UserDO::getName).toList()));
	}

	// -------- 更新与删除（使用事务回滚不影响其它用例） --------

	@Test
	@Transactional
	void replaceColumnValue_updates_expected_rows() {
		boolean updatedNulls = userRepo.replaceColumnValue(UserDO::getEmail, "unknown@example.com", null);
		assertTrue(updatedNulls);
		assertEquals(0, userRepo.listByColumnNull(UserDO::getEmail).size());
	}

	@Test
	@Transactional
	void removeByColumnValue_and_values_delete_rows() {
		boolean removedSingle = userRepo.removeByColumnValue(UserDO::getAge, 28);
		assertTrue(removedSingle);
		assertTrue(userRepo.listByColumnValue(UserDO::getAge, 28).isEmpty());

		boolean removedMulti = userRepo.removeByColumnValues(UserDO::getAge, List.of(25, 30));
		assertTrue(removedMulti);
		assertTrue(userRepo.listByColumnValues(UserDO::getAge, List.of(25, 30)).isEmpty());
	}

	@Test
	@Transactional
	void remove_like_and_notLike_variants() {
		// 1) 先删除不以 'Al' 开头的（命中 Bob、Carol）
		assertTrue(userRepo.removeByColumnNotLikeRight(UserDO::getName, "Al"));
		assertTrue(userRepo.listByColumnNotLikeRight(UserDO::getName, "Al").isEmpty());

		// 2) 删除包含 'Ali' 的（命中两条 Alice）
		assertTrue(userRepo.removeByColumnLike(UserDO::getName, "Ali"));
		assertTrue(userRepo.listByColumnLikeColumn(UserDO::getName, "Ali").isEmpty());

		// 为了演示 left/right，再插入一条 Carol 以测试 likeLeft
		UserDO carol = new UserDO();
		carol.setId(999L);
		carol.setName("Carol");
		carol.setAge(25);
		userRepo.save(carol);

		// 3) 删除以 'ol' 结尾的（命中 Carol）
		assertTrue(userRepo.removeByColumnLikeLeft(UserDO::getName, "ol"));
		assertTrue(userRepo.listByColumnLikeLeft(UserDO::getName, "ol").isEmpty());

		// 为了演示 notLike（包含 'c' 的），再插入一条 Bob
		UserDO bob = new UserDO();
		bob.setId(1000L);
		bob.setName("Bob");
		bob.setAge(25);
		userRepo.save(bob);

		// 4) 删除不包含 'c' 的（命中 Bob）
		assertTrue(userRepo.removeByColumnNotLike(UserDO::getName, "c"));
		assertTrue(userRepo.listByColumnNotLike(UserDO::getName, "c").isEmpty());
	}

	// -------- JSON 方法（Lambda 与列名重载） --------

	@Test
	void emptyJsonObject_and_array() {
		var emptyObj = docRepo.listByColumnEmptyJsonObject(DocDO::getMeta);
		assertEquals(Set.of("doc3", "doc4"), Set.copyOf(emptyObj.stream().map(DocDO::getTitle).toList()));

		var emptyArr = docRepo.listByColumnEmptyJsonArray(DocDO::getTags);
		assertEquals(Set.of("doc2", "doc4"), Set.copyOf(emptyArr.stream().map(DocDO::getTitle).toList()));
	}

	@Test
	void jsonKey_and_keyValue_lambda_overloads() {
		var hasAuthor = docRepo.listByColumnJsonKey(DocDO::getMeta, "author");
		assertEquals(Set.of("doc1", "doc2"), Set.copyOf(hasAuthor.stream().map(DocDO::getTitle).toList()));

		var authorAlice = docRepo.listByColumnJsonKeyValue(DocDO::getMeta, "author", "Alice");
		assertEquals(Set.of("doc1"), Set.copyOf(authorAlice.stream().map(DocDO::getTitle).toList()));
	}

	@Test
	void jsonArray_value_and_values_lambda_overloads() {
		var containsNews = docRepo.listByColumnJsonArrayValue(DocDO::getTags, "news");
		assertEquals(Set.of("doc1"), Set.copyOf(containsNews.stream().map(DocDO::getTitle).toList()));

		var overlaps = docRepo.listByColumnJsonArrayValues(DocDO::getTags, List.of("tech", "misc"));
		assertEquals(Set.of("doc1", "doc3"), Set.copyOf(overlaps.stream().map(DocDO::getTitle).toList()));
	}

	@Test
	void jsonKey_and_keyValue_columnName_overloads() {
		var hasAuthor = docRepo.listByColumnJsonKey("meta", "author");
		assertEquals(Set.of("doc1", "doc2"), Set.copyOf(hasAuthor.stream().map(DocDO::getTitle).toList()));

		var authorBob = docRepo.listByColumnJsonKeyValue("meta", "author", "Bob");
		assertEquals(Set.of("doc2"), Set.copyOf(authorBob.stream().map(DocDO::getTitle).toList()));
	}

	@Test
	void jsonArray_value_and_values_columnName_overloads() {
		var containsTech = docRepo.listByColumnJsonArrayValue("tags", "tech");
		assertEquals(Set.of("doc1"), Set.copyOf(containsTech.stream().map(DocDO::getTitle).toList()));

		var overlaps = docRepo.listByColumnJsonArrayValues("tags", List.of("misc", "foo"));
		assertEquals(Set.of("doc3"), Set.copyOf(overlaps.stream().map(DocDO::getTitle).toList()));
	}
}
