package io.github.pangju666.framework.data.mongo;

import io.github.pangju666.framework.data.mongo.repository.TestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;

@DataMongoTest
public class BaseRepositoryTest {
	@Autowired
	MongoOperations mongoOperations;
	@Autowired
	TestRepository repository;

	@Test
	public void test() {
		mongoOperations.createCollection("test");
	}
}
