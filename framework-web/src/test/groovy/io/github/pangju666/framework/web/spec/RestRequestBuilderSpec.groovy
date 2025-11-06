package io.github.pangju666.framework.web.spec

import io.github.pangju666.commons.lang.utils.JsonUtils
import io.github.pangju666.framework.web.TestApplication
import io.github.pangju666.framework.web.client.RestRequestBuilder
import io.github.pangju666.framework.web.model.Result
import io.github.pangju666.framework.web.model.dto.TestDTO
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestClient
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class RestRequestBuilderSpec extends Specification {
	@Autowired
	RestClient restClient

	def "测试无响应体请求"() {
		when:
		def entity = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1/test-no-response")
			.method(HttpMethod.GET)
			.toBodilessEntity()

		then:
		entity.getBody() == null
		entity.getStatusCode() == HttpStatus.OK
	}

	def "测试路径参数、请求参数、请求头参数请求"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/test/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header"]
	}

	def "测试json请求体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-body/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.jsonBody(body)
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header", "test-key", "test-value"]

		where:
		body                                                                          | _
		new TestDTO("test-key", "test-value")                                         | _
		"{ \"key\": \"test-key\", \"value\": \"test-value\" }"                        | _
		"{ \"key\": \"test-key\", \"value\": \"test-value\" }".getBytes()             | _
		JsonUtils.parseString("{ \"key\": \"test-key\", \"value\": \"test-value\" }") | _
	}

	def "测试text请求体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-text/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.textBody("test-text")
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header", "test-text"]
	}

	def "测试bytes请求体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-bytes/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.bytesBody(new File("src/test/resources/images/test.jpg").readBytes())
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header", Base64.encodeBase64String(new File("src/test/resources/images/test.jpg").readBytes())]
	}

	def "测试resource请求体"() {
		when:
		def image = new File("src/test/resources/images/test.jpg")
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-resource/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.resourceBody(new FileSystemResource(image))
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header", image.size().toString()]
	}

	def "测试form请求体"() {
		when:
		def image = new File("src/test/resources/images/test.jpg")
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.POST)
			.path("/test-form/{path}")
			.queryParam("test-param", "test-param")
			.uriVariable("path", "test-path-variable")
			.header("test-header", "test-header")
			.formPart("file", new FileSystemResource(image))
			.toEntity(new ParameterizedTypeReference<Result<List<String>>>() {})
			.getBody()

		then:
		result.getData() == ["test-path-variable", "test-param", "test-header", image.size().toString()]
	}

	def "测试json响应体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/json")
			.toJsonEntity(new ParameterizedTypeReference<Result<String>>() {})
			.getBody()

		then:
		result.getData() == "json"
	}

	def "测试string响应体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/str")
			.toStringEntity(MediaType.APPLICATION_OCTET_STREAM)
			.getBody()

		then:
		result == "string"
	}

	def "测试bytes响应体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/bytes")
			.toBytesEntity()
			.getBody()

		then:
		result == "bytes".getBytes()
	}

	def "测试bytes响应体2"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/str")
			.toBytesEntity()
			.getBody()

		then:
		result == "string".getBytes()
	}

	def "测试resource响应体"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/resource")
			.toResourceEntity()
			.getBody()

		then:
		result.getContentAsByteArray().size() == new File("src/test/resources/images/test.jpg").readBytes().size()
	}

	def "测试resource响应体2"() {
		when:
		def result = RestRequestBuilder.fromUriString(restClient, "http://127.0.0.1")
			.method(HttpMethod.GET)
			.path("/response/bytes")
			.toResourceEntity()
			.getBody()

		then:
		result.getContentAsByteArray() == "bytes".getBytes()
	}

	def "asdad"() {
		setup:
		println RestRequestBuilder.fromUrlString(restClient, "http://127.0.0.1/test-a")
			.toJson(Result.class)
	}
}