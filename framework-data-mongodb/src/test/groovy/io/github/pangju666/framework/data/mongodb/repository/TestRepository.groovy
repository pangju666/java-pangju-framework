package io.github.pangju666.framework.data.mongodb.repository

import io.github.pangju666.framework.data.mongodb.model.TestDocument
import org.springframework.stereotype.Repository

@Repository
interface TestRepository extends BaseMongoRepository<TestDocument, String> {
}
