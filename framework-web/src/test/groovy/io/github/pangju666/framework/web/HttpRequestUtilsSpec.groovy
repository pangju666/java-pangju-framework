package io.github.pangju666.framework.web

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.reflect.TypeToken
import io.github.pangju666.framework.web.lang.WebConstants
import io.github.pangju666.framework.web.servlet.utils.HttpRequestUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockPart
import org.springframework.util.MultiValueMap
import org.springframework.web.util.ContentCachingRequestWrapper
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Config)
class HttpRequestUtilsSpec extends Specification {
	@SpringBootConfiguration
	@EnableAutoConfiguration
	static class Config {
		// 最小Spring Boot上下文，无需Bean
	}

	// ---------------- 设备识别 / Ajax ----------------

	def "isFormMobile 对常见移动UA返回 true；Windows/Mac 返回 false"() {
		given:
		def mobile = new MockHttpServletRequest()
		mobile.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Linux; Android 9; Pixel 3) AppleWebKit/537.36 Chrome/90 Mobile Safari/537.36 Android")

		def windows = new MockHttpServletRequest()
		windows.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36")

		def mac = new MockHttpServletRequest()
		mac.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 Version/17.4 Safari/605.1.15")

		expect:
		HttpRequestUtils.isFormMobile(mobile)
		!HttpRequestUtils.isFormMobile(windows)
		!HttpRequestUtils.isFormMobile(mac)
	}

	@Unroll
	def "isFromAjax 多种线索识别 Ajax: #caseName"(String caseName, MockHttpServletRequest request) {
		expect:
		HttpRequestUtils.isFromAjax(request)

		where:
		caseName                             | request
		"Accept 包含 JSON"                   | new MockHttpServletRequest().tap { addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE) }
		"X-Requested-With 是 XMLHttpRequest" | new MockHttpServletRequest().tap { addHeader("X-Requested-With", "XMLHttpRequest") }
		"URI 后缀 .json"                     | new MockHttpServletRequest().tap { setRequestURI("/api/data.json") }
		"参数 __ajax=json"                   | new MockHttpServletRequest().tap { setParameter("__ajax", "json") }
	}

	def "isFromAjax 未命中任何线索返回 false"() {
		expect:
		!HttpRequestUtils.isFromAjax(new MockHttpServletRequest())
	}

	// ---------------- 客户端 IP ----------------

	def "getIpAddress 头部优先级与多级代理处理"() {
		given:
		def req = new MockHttpServletRequest()
		req.addHeader("x-forwarded-for", "203.0.113.7, unknown, 10.0.0.1")

		expect:
		HttpRequestUtils.getIpAddress(req) == "203.0.113.7"
	}

	def "getIpAddress 依次回退各代理头，最后使用 remoteAddr；本地地址标准化为 127.0.0.1"() {
		given:
		def req1 = new MockHttpServletRequest()
		// 所有代理头缺失，remoteAddr 为 IPv6 本地
		req1.setRemoteAddr("::1")

		def req2 = new MockHttpServletRequest()
		req2.addHeader("x-forwarded-for", "unknown")
		req2.addHeader("Proxy-Client-IP", "198.51.100.23")

		expect:
		HttpRequestUtils.getIpAddress(req1) == WebConstants.LOCAL_HOST_IPV4_ADDRESS
		HttpRequestUtils.getIpAddress(req2) == "198.51.100.23"
	}

	// ---------------- 参数 / 头 ----------------

	def "getParameters 保持值顺序并返回只读 MultiValueMap"() {
		given:
		def req = new MockHttpServletRequest()
		req.setParameter("a", "1")
		req.addParameter("b", "x")
		req.addParameter("b", "y")

		when:
		MultiValueMap<String, String> params = HttpRequestUtils.getParameters(req)

		then:
		params.get("a") == ["1"]
		params.get("b") == ["x", "y"]

		when: "尝试修改只读返回"
		params.add("c", "z")

		then:
		thrown(UnsupportedOperationException)
	}

	def "getHeaders 收集多值头并返回只读 HttpHeaders"() {
		given:
		def req = new MockHttpServletRequest()
		req.addHeader("X-Multi", "v1")
		req.addHeader("X-Multi", "v2")
		req.addHeader("X-Single", "s1")

		when:
		def headers = HttpRequestUtils.getHeaders(req)

		then:
		headers.getValuesAsList("X-Multi") == ["v1", "v2"]
		headers.getFirst("X-Single") == "s1"

		when:
		headers.add("X-Another", "boom")

		then:
		thrown(UnsupportedOperationException)
	}

	// ---------------- 文件上传 ----------------

	def "getParts 仅收集有提交文件名的 Part；非 multipart 返回空"() {
		given:
		def req = new MockHttpServletRequest()
		// 非 multipart
		req.setContentType(MediaType.TEXT_PLAIN_VALUE)

		expect:
		HttpRequestUtils.getParts(req).isEmpty()

		when:
		// multipart 且包含一个文件 Part 和一个普通字段 Part（无 filename）
		def mreq = new MockHttpServletRequest()
		mreq.setContentType("multipart/form-data; boundary=BOUNDARY")
		mreq.addPart(new MockPart("file", "demo.txt", "abc".getBytes()))
		mreq.addPart(new MockPart("field", "xyz".getBytes())) // 无文件名

		def parts = HttpRequestUtils.getParts(mreq)

		then:
		parts.size() == 1
		parts.containsKey("file")
		parts.get("file").getSubmittedFileName() == "demo.txt"
	}

	// ---------------- 请求体读取 / 字符集 ----------------

	def "getRawRequestBody 包装器未读取时返回空；读取后返回缓存内容；非包装器直接读取"() {
		given:
		byte[] payload = "你好, world".getBytes("UTF-8")
		def req = new MockHttpServletRequest()
		req.setContent(payload)

		and:
		def wrapper = new ContentCachingRequestWrapper(req)

		expect: "未读取时缓存为空"
		HttpRequestUtils.getRawRequestBody(wrapper).length == 0

		when: "读取触发缓存"
		wrapper.getInputStream().readAllBytes()

		then:
		HttpRequestUtils.getRawRequestBody(wrapper) == payload

		and: "非包装器直接读取"
		HttpRequestUtils.getRawRequestBody(new MockHttpServletRequest().tap {
			setContent("abc".getBytes("UTF-8"))
		}) == "abc".getBytes("UTF-8")
	}

	def "getStringRequestBody 按请求字符集解码；非法/不受支持字符集回退为 UTF-8"() {
		given:
		def req1 = new MockHttpServletRequest()
		req1.setCharacterEncoding("ISO-8859-1")
		req1.setContent("hello".getBytes("ISO-8859-1"))

		def req2 = new MockHttpServletRequest()
		req2.setCharacterEncoding("BAD_CHARSET")
		req2.setContent("中文".getBytes("UTF-8"))

		expect:
		HttpRequestUtils.getStringRequestBody(req1) == "hello"
		HttpRequestUtils.getStringRequestBody(req2) == "中文"
	}

	// ---------------- JSON 解析 ----------------

	def "getJsonRequestBody(JsonElement) 非JSON返回 JsonNull；JSON 返回解析后的元素"() {
		given:
		def req1 = new MockHttpServletRequest()
		req1.setContentType(MediaType.TEXT_PLAIN_VALUE)
		req1.setContent("x=1".bytes)

		def req2 = new MockHttpServletRequest()
		req2.setContentType("${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8")
		req2.setContent('{"a":1,"b":"x"}'.getBytes("UTF-8"))

		when:
		JsonElement el1 = HttpRequestUtils.getJsonRequestBody(req1)
		JsonElement el2 = HttpRequestUtils.getJsonRequestBody(req2)

		then:
		el1.is(JsonNull.INSTANCE)
		el2.isJsonObject()
		el2.getAsJsonObject().get("a").getAsInt() == 1
		el2.getAsJsonObject().get("b").getAsString() == "x"
	}

	def "getJsonRequestBody(Class) 非JSON返回 null；JSON 解析为指定类型"() {
		given:
		def req = new MockHttpServletRequest()
		req.setContentType("${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8")
		req.setContent('{"k":"v","n":2}'.getBytes("UTF-8"))

		when:
		Map obj = HttpRequestUtils.getJsonRequestBody(req, Map.class)

		then:
		obj.get("k") == "v"
		obj.get("n") == 2

		and:
		HttpRequestUtils.getJsonRequestBody(new MockHttpServletRequest().tap {
			setContentType(MediaType.TEXT_PLAIN_VALUE)
			setContent("x=1".bytes)
		}, Map.class) == null
	}

	def "getJsonRequestBody(TypeToken) 非JSON返回 null；JSON 解析为泛型类型"() {
		given:
		def req = new MockHttpServletRequest()
		req.setContentType("${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8")
		req.setContent('[1,2,3]'.getBytes("UTF-8"))

		when:
		List<Integer> list = HttpRequestUtils.getJsonRequestBody(req, new TypeToken<List<Integer>>() {})

		then:
		list == [1, 2, 3]

		and:
		HttpRequestUtils.getJsonRequestBody(new MockHttpServletRequest().tap {
			setContentType(MediaType.TEXT_PLAIN_VALUE)
			setContent("x".bytes)
		}, new TypeToken<List<Integer>>() {}) == null
	}

	// ---------------- JSON 类型判定 ----------------

	@Unroll
	def "isJsonRequestBody 判断类型/子类型，忽略参数: #contentType"(String contentType, boolean expected) {
		given:
		def req = new MockHttpServletRequest()
		if (contentType != null) {
			req.setContentType(contentType)
		}

		expect:
		HttpRequestUtils.isJsonRequestBody(req) == expected

		where:
		contentType                                         | expected
		MediaType.APPLICATION_JSON_VALUE                    | true
		"${MediaType.APPLICATION_JSON_VALUE};charset=UTF-8" | true
		MediaType.TEXT_PLAIN_VALUE                          | false
		null                                                | false
		""                                                  | false
	}

	// ---------------- 非法参数校验 ----------------

	@Unroll
	def "所有声明非空的入口在传入 null 时抛 IllegalArgumentException: #methodName"(String methodName, Closure call) {
		when:
		call.call()

		then:
		thrown(IllegalArgumentException)

		where:
		methodName                        | call
		"isFormMobile"                    | { HttpRequestUtils.isFormMobile(null) }
		"isFromAjax"                      | { HttpRequestUtils.isFromAjax(null) }
		"getIpAddress"                    | { HttpRequestUtils.getIpAddress(null) }
		"getParameters"                   | { HttpRequestUtils.getParameters(null) }
		"getHeaders"                      | { HttpRequestUtils.getHeaders(null) }
		"getParts"                        | { HttpRequestUtils.getParts(null as HttpServletRequest) }
		"getStringRequestBody"            | { HttpRequestUtils.getStringRequestBody(null) }
		"getJsonRequestBody(JsonElement)" | { HttpRequestUtils.getJsonRequestBody(null as HttpServletRequest) }
		"getJsonRequestBody(Class)"       | { HttpRequestUtils.getJsonRequestBody(null as HttpServletRequest, Map.class) }
		"getJsonRequestBody(TypeToken)"   | { HttpRequestUtils.getJsonRequestBody(null as HttpServletRequest, new TypeToken<List<Integer>>() {}) }
		"isJsonRequestBody"               | { HttpRequestUtils.isJsonRequestBody(null) }
	}
}
