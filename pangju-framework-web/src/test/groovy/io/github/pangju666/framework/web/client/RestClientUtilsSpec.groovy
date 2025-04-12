package io.github.pangju666.framework.web.client

import com.google.gson.JsonObject
import io.github.pangju666.framework.http.utils.RestClientUtils
import io.github.pangju666.framework.web.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class RestClientUtilsSpec extends Specification {
	@Autowired
	RestClient restClient

	URI testUri
	File testFile

	def setup() {
		restClient = RestClient.create()
		requestBodySpec = Mock(RestClient.RequestBodySpec)
		testUri = new URI("http://test.com/api")
		testFile = File.createTempFile("test", ".txt")
		testFile.deleteOnExit()
	}

	def "toInputStreamEntity应正确处理输入流响应"() {
		given: "设置模拟响应"
		requestBodySpec.accept(MediaType.APPLICATION_OCTET_STREAM) >> requestBodySpec
		requestBodySpec.retrieve() >> Mock(RestClient.ResponseSpec)

		when: "调用方法"
		def result = RestClientUtils.toInputStreamEntity(requestBodySpec)

		then: "验证响应处理"
		result instanceof ResponseEntity
		1 * requestBodySpec.retrieve().toEntity(InputStream.class)
	}

	def "toBytesEntity应正确处理字节数组响应"() {
		given: "设置模拟响应"
		requestBodySpec.accept(MediaType.APPLICATION_OCTET_STREAM) >> requestBodySpec
		requestBodySpec.retrieve() >> Mock(RestClient.ResponseSpec)

		when: "调用方法"
		def result = RestClientUtils.toBytesEntity(requestBodySpec)

		then: "验证响应处理"
		result instanceof ResponseEntity
		1 * requestBodySpec.retrieve().toEntity(byte[].class)
	}

	def "toResultEntity应正确处理Result包装的响应"() {
		given: "设置模拟响应"
		requestBodySpec.accept(MediaType.APPLICATION_JSON) >> requestBodySpec
		requestBodySpec.acceptCharset(_) >> requestBodySpec
		requestBodySpec.retrieve() >> Mock(RestClient.ResponseSpec)

		when: "调用方法"
		def result = RestClientUtils.toResultEntity(requestBodySpec)

		then: "验证响应处理"
		result instanceof ResponseEntity
		1 * requestBodySpec.retrieve().toEntity(_ as ParameterizedTypeReference)
	}

	def "toJSONEntity应正确处理JSON响应"() {
		given: "设置模拟响应"
		requestBodySpec.accept(MediaType.APPLICATION_JSON) >> requestBodySpec
		requestBodySpec.retrieve() >> Mock(RestClient.ResponseSpec)

		when: "调用方法"
		def result = RestClientUtils.toJSONEntity(requestBodySpec, String.class)

		then: "验证响应处理"
		result instanceof ResponseEntity
		1 * requestBodySpec.retrieve().toEntity(String.class)
	}

	@Unroll
	def "buildRequestBodySpec应正确处理不同类型的请求：#httpMethod"() {
		given: "准备请求参数"
		def headers = new LinkedMultiValueMap<String, String>()
		def queryParams = new LinkedMultiValueMap<String, String>()
		def uriVariables = [id: "1"]
		def requestBody = [name: "test"]

		when: "构建请求规范"
		def result = RestClientUtils.buildRequestBodySpec(
			restClient,
			testUri,
			httpMethod,
			headers,
			queryParams,
			uriVariables,
			requestBody
		)

		then: "验证请求规范"
		result instanceof RestClient.RequestBodySpec

		where:
		httpMethod << [HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE]
	}

	def "buildRequestBodySpec应正确处理文件上传"() {
		given: "准备包含文件的请求体"
		def requestBody = [file: testFile]

		when: "构建请求规范"
		def result = RestClientUtils.buildRequestBodySpec(
			restClient,
			testUri,
			HttpMethod.POST,
			null,
			null,
			null,
			requestBody
		)

		then: "验证请求规范"
		result instanceof RestClient.RequestBodySpec
	}

	def "buildRequestBodySpec应正确处理查询参数"() {
		given: "准备查询参数"
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>()
		queryParams.add("key", "value")

		when: "构建请求规范"
		def result = RestClientUtils.buildRequestBodySpec(
			restClient,
			testUri,
			HttpMethod.GET,
			null,
			queryParams,
			null,
			null
		)

		then: "验证请求规范"
		result instanceof RestClient.RequestBodySpec
	}

	def "buildRequestBodySpec应正确处理JsonElement请求体"() {
		given: "准备JSON请求体"
		def jsonBody = new JsonObject()
		jsonBody.addProperty("key", "value")

		when: "构建请求规范"
		def result = RestClientUtils.buildRequestBodySpec(
			restClient,
			testUri,
			HttpMethod.POST,
			null,
			null,
			null,
			jsonBody
		)

		then: "验证请求规范"
		result instanceof RestClient.RequestBodySpec
	}

	def "buildRequestBodySpec应对null参数抛出异常"() {
		when: "传入null参数"
		RestClientUtils.buildRequestBodySpec(null, testUri, HttpMethod.GET, null, null, null, null)

		then: "抛出IllegalArgumentException"
		thrown(IllegalArgumentException)
	}

	def cleanup() {
		testFile.delete()
	}
}
