package io.github.pangju666.framework.data.mybatisplus.spec


import io.github.pangju666.framework.data.mybatisplus.TestApplication
import io.github.pangju666.framework.data.mybatisplus.repository.TestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class BaseRepositoryTest2 extends Specification {
	@Autowired
	TestRepository repository

	def "test"() {
		setup:
		def list = repository.list()
		println 1
	}
}
