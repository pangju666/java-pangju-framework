package io.github.pangju666.framework.data.mybatisplus.test;

import io.github.pangju666.framework.data.mybatisplus.repository.TestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TmpBaseRepositoryTest {
	@Autowired
	TestRepository repository;

	@Test
	public void test() {
		List<String> names = List.of("test1", "test2");
		var list = repository.removeByIds(names);
		System.out.println(list);
	}
}
