package io.github.pangju666.framework.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.pangju666.commons.lang.utils.DateUtils
import io.github.pangju666.framework.core.enums.TestEnum
import io.github.pangju666.framework.core.model.TestData
import org.springframework.boot.SpringBootConfiguration
import spock.lang.Specification

@SpringBootConfiguration
class JacksonSerializerSpec extends Specification {
	ObjectMapper mapper = new ObjectMapper()

	def "test serialize"() {
		setup:
		def object = new TestData()
		object.value = TestEnum.TEST
		object.phoneNumber = "13336111326"
		object.date = new Date()
		object.localDate = DateUtils.toLocalDate(new Date())
		object.localDateTime = DateUtils.toLocalDateTime(new Date())
		object.aClass = TestData.class
		object.decimal = new BigDecimal("123456789.123456789")

		def json = mapper.writer().writeValueAsString(object)
		println json
	}

	def "test serialize null"() {
		setup:
		def object = new TestData()
		object.value = TestEnum.TEST
		object.phoneNumber = "1"
		object.date = null
		object.localDate = null
		object.localDateTime = null

		def json = mapper.writer().writeValueAsString(object)
		println json
	}
}
