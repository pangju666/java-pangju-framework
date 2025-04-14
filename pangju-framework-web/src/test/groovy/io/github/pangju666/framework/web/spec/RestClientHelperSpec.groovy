package io.github.pangju666.framework.web.spec

import io.github.pangju666.commons.lang.utils.JsonUtils
import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.client.RestClientHelper
import io.github.pangju666.framework.web.model.dto.TestDTO
import io.github.pangju666.framework.web.model.vo.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class RestClientHelperSpec extends Specification {
	@Autowired
	RestClient restClient

	def "test1"() {
		when:
		def entity = RestClientHelper.fromUriString(restClient, "http://127.0.0.1/test-no-params")
			.method(HttpMethod.GET)
			.toBodilessEntity()

		then:
		entity.getBody() == null
		entity.getStatusCode() == HttpStatus.OK
	}

	def "test2"() {
		when:
		def result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/test-params/{path}")
			.queryParam("param1", "test")
			.uriVariable("path", "test")
			.toEntity(new ParameterizedTypeReference<Result<Map<String, String>>>() {})
			.getBody()

		then:
		result.data().get("param1") == "test"
		result.data().get("path") == "test"
	}


	def "test3"() {
		when:
		def result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParam("param1", "test")
			.jsonBody(new TestDTO("body-key", "body-value"))
			.uriVariable("path", "test")
			.toEntity(new ParameterizedTypeReference<Result<Map<String, String>>>() {})
			.getBody()

		then:
		result.data().get("param1") == "test"
		result.data().get("path") == "test"
		result.data().get("body-key") == "body-value"
	}

	def "test4"() {
		when:
		def result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParams(Collections.singletonMap("param1", "test"))
			.jsonBody(JsonUtils.toJson(new TestDTO("body-key", "body-value")))
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Result<Map<String, String>>>() {})
			.getBody()

		then:
		result.data().get("param1") == "test"
		result.data().get("path") == "test"
		result.data().get("body-key") == "body-value"
	}

	def "test5"() {
		when:
		def result = RestClientHelper.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParams(Collections.singletonMap("param1", "test"))
			.jsonBody("{ \"key\": \"body-key\", \"value\": \"body-value\" }")
			.uriVariables(Collections.singletonMap("path", "test"))
			.toEntity(new ParameterizedTypeReference<Result<Map<String, String>>>() {})
			.getBody()

		then:
		result.data().get("param1") == "test"
		result.data().get("path") == "test"
		result.data().get("body-key") == "body-value"
	}
}
