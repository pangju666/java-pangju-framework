package io.github.pangju666.framework.core.jackson.databind.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.pangju666.framework.core.jackson.TestData
import org.springframework.boot.SpringBootConfiguration
import spock.lang.Specification

@SpringBootConfiguration
class EnumJsonDeserializerSpec extends Specification {
	ObjectMapper mapper = new ObjectMapper()

	def "Feature method"() {
		setup:
		def object = mapper.reader().readValue("{\"value\":\"TEST\"}", TestData.class)
		println object
	}
}
