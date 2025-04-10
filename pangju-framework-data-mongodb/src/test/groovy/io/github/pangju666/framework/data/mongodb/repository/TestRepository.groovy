package io.github.pangju666.framework.data.mongodb.repository

import io.github.pangju666.framework.data.mongodb.model.TestDocument
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Repository

@Repository
class TestRepository extends BaseRepository<TestDocument> {
	TestRepository(MongoOperations mongoOperations) {
		super(mongoOperations)
	}
}
