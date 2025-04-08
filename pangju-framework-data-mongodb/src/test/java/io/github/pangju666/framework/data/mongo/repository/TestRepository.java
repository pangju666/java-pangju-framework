package io.github.pangju666.framework.data.mongo.repository;

import io.github.pangju666.framework.data.mongo.model.TestDocument;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository extends BaseRepository<TestDocument> {
	public TestRepository(MongoOperations mongoOperations) {
		super(mongoOperations);
	}
}
