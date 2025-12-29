package io.github.pangju666.framework.web.client

import com.google.gson.JsonObject
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceException
import io.github.pangju666.framework.web.exception.remote.HttpRemoteServiceTimeoutException
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError
import org.springframework.http.*
import org.springframework.http.client.ClientHttpResponse
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.StandardCharsets

class JsonResponseErrorHandlerSpec extends Specification {
	/**
	 * 简单的 ClientHttpResponse 实现，便于构造不同响应场景。
	 */
	static class FakeClientHttpResponse implements ClientHttpResponse {
		private final HttpStatus status
		private final HttpHeaders headers
		private final byte[] body

		FakeClientHttpResponse(HttpStatus status, MediaType contentType, byte[] body) {
			this.status = status
			this.headers = new HttpHeaders()
			if (contentType != null) {
				this.headers.setContentType(contentType)
			}
			this.body = body ?: new byte[0]
		}

		@Override
		String getStatusText() { status.reasonPhrase }

		@Override
		void close() { /* no-op */ }

		@Override
		HttpStatusCode getStatusCode() { status }

		@Override
		HttpHeaders getHeaders() { headers }

		@Override
		InputStream getBody() { new ByteArrayInputStream(body) }
	}

	private static byte[] jsonBytes(Map<String, Object> map) {
		def jo = new JsonObject()
		map.each { k, v ->
			if (v == null) return
			if (v instanceof Number) jo.addProperty(k, (Number) v)
			else if (v instanceof Boolean) jo.addProperty(k, (Boolean) v)
			else jo.addProperty(k, String.valueOf(v))
		}
		jo.toString().getBytes(StandardCharsets.UTF_8)
	}

	def "hasError: 5xx 错误状态应返回 true"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		def resp = new FakeClientHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, MediaType.APPLICATION_JSON, jsonBytes([code: "E1", message: "error"]))

		expect:
		handler.hasError(resp)
	}

	def "hasError: 非错误但非 200（201 Created）应返回 false"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		def resp = new FakeClientHttpResponse(HttpStatus.CREATED, MediaType.APPLICATION_JSON, jsonBytes([code: "OK"]))

		expect:
		!handler.hasError(resp)
	}

	def "hasError: 200 OK 且非缓冲包装应返回 false（需 Buffering 才进行业务判定）"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		def resp = new FakeClientHttpResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, jsonBytes([code: "BAD", message: "业务失败"]))

		expect:
		!handler.hasError(resp)
	}

	def "handleError: 错误状态 + 非 JSON 响应体应仅封装状态并抛出 HttpRemoteServiceException"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		handler.setService("用户服务")
		handler.setApi("获取用户")
		def url = URI.create("http://example.com/api/users")
		def resp = new FakeClientHttpResponse(HttpStatus.BAD_REQUEST, MediaType.TEXT_PLAIN, "oops".bytes)

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		e.message != null
		with(e.getError() as HttpRemoteServiceError) {
			getService() == "用户服务"
			getApi() == "获取用户"
			it.getUrl() == url
			getHttpStatus().value() == HttpStatus.BAD_REQUEST.value()
			getCode() == null
			getMessage() == null
		}
	}

	def "handleError: 错误状态 + JSON 响应体提取 code/message（数值→字符串）"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		handler.setService("账单服务")
		handler.setApi("创建账单")
		def url = URI.create("http://example.com/api/bills")
		def body = jsonBytes([code: 400, message: "参数错误"])
		def resp = new FakeClientHttpResponse(HttpStatus.BAD_REQUEST, MediaType.parseMediaType("application/json;charset=UTF-8"), body)

		when:
		handler.handleError(url, HttpMethod.POST, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		with(e.getError() as HttpRemoteServiceError) {
			getCode() == "400"
			getMessage() == "参数错误"
			getHttpStatus().value() == 400
		}
	}

	@Unroll
	def "handleError: 非错误状态（200）也抛业务异常，提取不同类型的 code [#desc]"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		handler.setService("订单服务")
		handler.setApi("查询订单")
		def url = URI.create("http://example.com/api/orders")
		def resp = new FakeClientHttpResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, jsonBytes(payload))

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		with(e.getError() as HttpRemoteServiceError) {
			getCode() == expectedCode
			getMessage() == expectedMessage
			getHttpStatus().value() == 200
		}

		where:
		payload                         || expectedCode | expectedMessage | desc
		[code: "E001", message: "失败"] || "E001"       | "失败"          | "字符串 code"
		[code: 1, message: "失败"]      || "1"          | "失败"          | "数值 code"
		[code: true, message: "失败"]   || "true"       | "失败"          | "布尔 code"
		[message: "失败"]               || null         | "失败"          | "无 code 字段"
		[code: "E1"]                    || "E1"         | null            | "无 message 字段"
	}

	def "handleError: 504 Gateway Timeout 映射为 HttpRemoteServiceTimeoutException"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		def url = URI.create("http://example.com/api/timeouts")
		def resp = new FakeClientHttpResponse(HttpStatus.GATEWAY_TIMEOUT, MediaType.APPLICATION_JSON, jsonBytes([code: "TIMEOUT", message: "超时"]))

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceTimeoutException)
		with(e.getError() as HttpRemoteServiceError) {
			getHttpStatus().value() == 504
		}
	}

	def "handleError: 自定义异常消息 customExceptionMessage 优先覆盖异常消息"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		handler.setCustomExceptionMessage("统一异常提示")
		def url = URI.create("http://example.com/api/test")
		def resp = new FakeClientHttpResponse(HttpStatus.BAD_REQUEST, MediaType.APPLICATION_JSON, jsonBytes([code: "E", message: "原始消息"]))

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		e.message == "统一异常提示"
	}

	def "配置锁定：init 后 codeField/messageField 不再生效"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		handler.setCodeField("xCode")
		handler.setMessageField("xMsg")
		handler.init() // 锁定配置
		// 尝试修改（应被忽略）
		handler.setCodeField("otherCode")
		handler.setMessageField("otherMsg")
		def url = URI.create("http://example.com/api/locked")
		// 响应体使用原始字段名，验证修改被忽略
		def resp = new FakeClientHttpResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, jsonBytes([xCode: "L1", xMsg: "锁定后仍使用旧字段"]))

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		with(e.getError() as HttpRemoteServiceError) {
			getCode() == "L1"
			getMessage() == "锁定后仍使用旧字段"
		}
	}

	def "getResponseBody 行为：非 JSON Content-Type 时不解析响应体（message/code 为 null）"() {
		given:
		def handler = new JsonResponseErrorHandler("OK")
		def url = URI.create("http://example.com/api/non-json")
		def resp = new FakeClientHttpResponse(HttpStatus.BAD_REQUEST, MediaType.TEXT_PLAIN, jsonBytes([code: "E1", message: "不会被解析"]))

		when:
		handler.handleError(url, HttpMethod.GET, resp)

		then:
		def e = thrown(HttpRemoteServiceException)
		with(e.getError() as HttpRemoteServiceError) {
			getCode() == null
			getMessage() == null
		}
	}
}
