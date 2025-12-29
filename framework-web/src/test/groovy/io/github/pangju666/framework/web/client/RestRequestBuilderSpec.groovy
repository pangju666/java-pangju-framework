package io.github.pangju666.framework.web.client


import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient
import spock.lang.Specification

import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Boot)
class RestRequestBuilderSpec extends Specification {
	@Autowired
	ApplicationContext applicationContext

	@LocalServerPort
	int port

	static class RecordingService {
		volatile String path
		volatile String query
		volatile String method
		volatile HttpHeaders headers = new HttpHeaders()
		volatile byte[] body = new byte[0]

		void record(HttpServletRequest req, HttpHeaders headers, byte[] body) {
			this.path = req.requestURI
			this.query = req.queryString ?: ""
			this.method = req.method
			this.headers = new HttpHeaders(headers)
			this.body = body ?: new byte[0]
		}
	}

	@RestController
	static class TestController {
		private final RecordingService recorder

		TestController(RecordingService recorder) {
			this.recorder = recorder
		}

		@GetMapping("/api/inspect")
		String inspectGet(HttpServletRequest req, @RequestHeader HttpHeaders headers) {
			recorder.record(req, headers, null)
			return "ok"
		}

		@PostMapping("/api/echo")
		ResponseEntity<byte[]> echo(HttpServletRequest req,
									@RequestHeader HttpHeaders headers,
									@RequestBody(required = false) byte[] body) {
			recorder.record(req, headers, body)
			MediaType ct = headers.getContentType() ?: MediaType.TEXT_PLAIN
			return ResponseEntity.ok().contentType(ct).body(body ?: new byte[0])
		}

		@GetMapping("/api/json")
		Map<String, Object> json(HttpServletRequest req, @RequestHeader HttpHeaders headers) {
			recorder.record(req, headers, null)
			return [key: "value"]
		}

		@GetMapping("/api/bytes")
		ResponseEntity<byte[]> bytes(HttpServletRequest req, @RequestHeader HttpHeaders headers) {
			recorder.record(req, headers, null)
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body("ABC".getBytes(StandardCharsets.UTF_8))
		}

		@GetMapping("/api/resource")
		ResponseEntity<Resource> resource(HttpServletRequest req, @RequestHeader HttpHeaders headers) {
			recorder.record(req, headers, null)
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new ByteArrayResource("R".getBytes(StandardCharsets.UTF_8)))
		}

		@GetMapping("/api/bodiless")
		ResponseEntity<Void> bodiless(HttpServletRequest req, @RequestHeader HttpHeaders headers) {
			recorder.record(req, headers, null)
			return ResponseEntity.noContent().build()
		}
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import([TestConfig, TestController])
	static class Boot {
		// empty, only enabling auto-configuration and importing test beans/controllers
	}

	@TestConfiguration
	static class TestConfig {
		@Bean
		RecordingService recordingService() { new RecordingService() }
	}

	private static RestClient restClient() {
		RestClient.builder().build()
	}

	private String baseUrl() {
		"http://localhost:$port"
	}

	def "path 追加与规范化，queryParam 多值展开与 Accept 设置"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)
		def b = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("api")
			.path("/inspect")
			.queryParam("page", 1)
			.queryParam("sort", "name", "asc")

		when:
		def resp = b.toString(MediaType.TEXT_PLAIN)

		then:
		resp == "ok"
		recorder.path == "/api/inspect"
		recorder.query == "page=1&sort=name&sort=asc"
		recorder.method == "GET"
		recorder.headers.getOrEmpty("Accept") == [MediaType.TEXT_PLAIN_VALUE]
	}

	def "query 原始字符串自动去除前导问号"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)
		def b = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/inspect")
			.query("?page=2&size=5")

		when:
		b.toString(MediaType.TEXT_PLAIN)

		then:
		recorder.path == "/api/inspect"
		recorder.query == "page=2&size=5"
	}

	def "headers 单值、数组与集合展开（服务端收到多个同名头）"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)
		def b = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/inspect")
			.header("X-Arr", ["a", "b"] as String[])
			.header("X-List", ["x", "y"])
			.header("X-One", "v")

		when:
		b.toString(MediaType.TEXT_PLAIN)

		then:
		recorder.headers.get("X-Arr") == ["a", "b"]
		recorder.headers.get("X-List") == ["x", "y"]
		recorder.headers.get("X-One") == ["v"]
	}

	def "method(POST) + jsonBody/textBody/bytesBody/resourceBody：请求体与 Content-Type 正确"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when: "jsonBody(null) 默认 {}"
		RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/echo")
			.method(HttpMethod.POST)
			.jsonBody(null)
			.toString(MediaType.TEXT_PLAIN)

		then:
		recorder.method == "POST"
		recorder.headers.getContentType().includes(MediaType.APPLICATION_JSON)
		new String(recorder.body, StandardCharsets.UTF_8) == "{}"

		when: "textBody(null) 默认空字符串"
		RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/echo")
			.method(HttpMethod.POST)
			.textBody(null)
			.toString(MediaType.TEXT_PLAIN)

		then:
		recorder.headers.getContentType().includes(MediaType.TEXT_PLAIN)
		new String(recorder.body, StandardCharsets.UTF_8) == ""

		when: "bytesBody(null) 默认空数组"
		RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/echo")
			.method(HttpMethod.POST)
			.bytesBody(null)
			.toBytes(MediaType.APPLICATION_OCTET_STREAM)

		then:
		recorder.headers.getContentType().includes(MediaType.APPLICATION_OCTET_STREAM)
		recorder.body.length == 0
		recorder.headers.get("Accept") == [MediaType.APPLICATION_OCTET_STREAM_VALUE]

		when: "resourceBody 回显二进制"
		def res = new ByteArrayResource("HELLO".getBytes(StandardCharsets.UTF_8))
		def echoed = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/echo")
			.method(HttpMethod.POST)
			.resourceBody(res)
			.toBytes(MediaType.APPLICATION_OCTET_STREAM)

		then:
		recorder.headers.getContentType().includes(MediaType.APPLICATION_OCTET_STREAM)
		new String(echoed, StandardCharsets.UTF_8) == "HELLO"
	}

	def "toJson(String.class) 返回 JSON 文本；Accept 为 application/json"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when:
		def jsonText = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/json")
			.toJson(String.class)

		then:
		jsonText == '{"key":"value"}'
		recorder.headers.get("Accept") == [MediaType.APPLICATION_JSON_VALUE]
	}

	def "toJson(ParameterizedTypeReference<String>) 返回 JSON 文本"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when:
		def jsonText = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/json")
			.toJson(new ParameterizedTypeReference<String>() {})

		then:
		jsonText == '{"key":"value"}'
		recorder.headers.get("Accept") == [MediaType.APPLICATION_JSON_VALUE]
	}

	def "toBean(ParameterizedTypeReference<String>, TEXT_PLAIN) 返回纯文本"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when:
		def text = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/inspect")
			.toBean(new ParameterizedTypeReference<String>() {}, MediaType.TEXT_PLAIN)

		then:
		text == "ok"
		recorder.headers.get("Accept") == [MediaType.TEXT_PLAIN_VALUE]
	}

	def "toBytes(OCTET_STREAM) 返回字节；toResourceEntity 返回 Resource 响应实体"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when:
		def bytes = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/bytes")
			.toBytes(MediaType.APPLICATION_OCTET_STREAM)

		then:
		new String(bytes, StandardCharsets.UTF_8) == "ABC"
		recorder.headers.get("Accept") == [MediaType.APPLICATION_OCTET_STREAM_VALUE]

		when:
		ResponseEntity<Resource> re = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/resource")
			.toResourceEntity(MediaType.APPLICATION_OCTET_STREAM)

		then:
		re.statusCode.is2xxSuccessful()
		new String(re.body.getInputStream().readAllBytes(), StandardCharsets.UTF_8) == "R"
	}

	def "toBodilessEntity 返回 204；Accept 不强制设置（允许空或 */*）"() {
		given:
		def recorder = applicationContext.getBean(RecordingService)

		when:
		def resp = RestRequestBuilder.fromUrlString(restClient(), baseUrl())
			.path("/api/bodiless")
			.toBodilessEntity()

		then:
		resp.statusCode.value() == 204
		// 过滤空白值，避免 parseMediaType("") 异常
		def accValues = recorder.headers.getOrEmpty(HttpHeaders.ACCEPT).findAll { it?.trim() }
		assert accValues.isEmpty() || accValues.any { v ->
			def mt = MediaType.parseMediaType(v)
			mt.equalsTypeAndSubtype(MediaType.ALL)  // 忽略参数，只比对类型/子类型
		}
	}
}