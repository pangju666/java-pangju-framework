package io.github.pangju666.framework.data.mongodb

import io.github.pangju666.framework.data.mongodb.repository.SimpleBaseMongoRepository
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(repositoryBaseClass = SimpleBaseMongoRepository.class)
@SpringBootApplication
class TestApplication {
	static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args)
	}
}
