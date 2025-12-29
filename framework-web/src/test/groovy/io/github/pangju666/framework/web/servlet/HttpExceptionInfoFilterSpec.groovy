package io.github.pangju666.framework.web.servlet

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.pangju666.framework.web.enums.HttpExceptionType
import io.github.pangju666.framework.web.lang.WebConstants
import io.github.pangju666.framework.web.servlet.filter.HttpExceptionInfoFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Config)
class HttpExceptionInfoFilterSpec extends Specification {

	@SpringBootConfiguration
	@EnableAutoConfiguration
	static class Config {
		// 最小化 Spring Boot 测试上下文
	}

	private final ObjectMapper mapper = new ObjectMapper()

	def "构造器参数校验：空 typesRequestPath 或 listRequestPath 抛出 IllegalArgumentException"() {
		when: "typesRequestPath 为空"
		new HttpExceptionInfoFilter("", "/http-exception/list", List.of("io.github.pangju666.framework.web.exception"))

		then:
		thrown(IllegalArgumentException)

		when: "listRequestPath 为空"
		new HttpExceptionInfoFilter("/http-exception/types", "   ", List.of("io.github.pangju666.framework.web.exception"))

		then:
		thrown(IllegalArgumentException)
	}

	def "types 端点返回所有 HttpExceptionType 的枚举项（label/value）"() {
		given:
		def filter = new HttpExceptionInfoFilter("/http-exception/types",
			"/http-exception/list", List.of("io.github.pangju666.framework.web.exception"))
		def request = new MockHttpServletRequest("GET", "/http-exception/types")
		request.setServletPath("/http-exception/types")
		def response = new MockHttpServletResponse()
		def chain = noOpChain()

		when:
		filter.doFilter(request, response, chain)

		then: "未调用后续链"
		!chain.invoked

		and: "响应可解析为 List<Map>"
		def list = parseList(response)
		list.size() == HttpExceptionType.values().length

		and: "包含所有枚举的 label/value 映射"
		def expected = HttpExceptionType.values().collect { [label: it.label, value: it.name()] } as Set
		def actual = list.collect { [label: it.label, value: it.value] } as Set
		actual == expected
	}

	def "list 端点返回异常信息列表，至少包含 UNKNOWN 默认项"() {
		given:
		def filter = new HttpExceptionInfoFilter("/http-exception/types",
			"/http-exception/list", List.of("io.github.pangju666.framework.web.exception"))
		def request = new MockHttpServletRequest("GET", "/http-exception/list")
		request.setServletPath("/http-exception/list")
		def response = new MockHttpServletResponse()
		def chain = noOpChain()

		when:
		filter.doFilter(request, response, chain)

		then: "未调用后续链"
		!chain.invoked

		and: "响应可解析为 List<Map> 且至少包含一项"
		def list = parseList(response)
		list.size() >= 1

		and: "存在 UNKNOWN 默认异常项（typeLabel/type/code/description）"
		def unknown = list.find { it.type == HttpExceptionType.UNKNOWN.name() }
		unknown != null
		unknown.typeLabel == HttpExceptionType.UNKNOWN.label
		unknown.code == WebConstants.BASE_ERROR_CODE
		unknown.description == null
	}

	def "非端点路径时透传到 FilterChain 且不写响应体"() {
		given:
		def filter = new HttpExceptionInfoFilter("/http-exception/types",
			"/http-exception/list", List.of("io.github.pangju666.framework.web.exception"))
		def request = new MockHttpServletRequest("GET", "/other-path")
		request.setServletPath("/other-path")
		def response = new MockHttpServletResponse()
		def chain = capturingChain()

		when:
		filter.doFilter(request, response, chain)

		then: "调用后续链"
		chain.invoked
		chain.callCount == 1

		and: "未写入响应体"
		response.contentAsByteArray.length == 0
		response.getContentType() == null
	}

	@Unroll
	def "包扫描参数支持空/空白/重复：packages=#packages"() {
		when:
		def filter = new HttpExceptionInfoFilter("/http-exception/types", "/http-exception/list", packages)

		then: "构造成功且 list 端点可工作"
		def request = new MockHttpServletRequest("GET", "/http-exception/list")
		request.setServletPath("/http-exception/list")
		def response = new MockHttpServletResponse()
		filter.doFilter(request, response, noOpChain())
		def list = parseList(response)
		list.size() >= 1 // 至少包含 UNKNOWN 默认项

		where:
		packages << ([
			null,
			List.of(),
			List.of("  ", "io.github.pangju666.framework.web.exception", "io.github.pangju666.framework.web.exception")
		])
	}

	// helpers

	private static CapturingChain capturingChain() {
		new CapturingChain()
	}

	private static NoOpChain noOpChain() {
		new NoOpChain()
	}

	// ... existing code ...
	private List<Map<String, Object>> parseList(MockHttpServletResponse response) {
		assert response.contentAsByteArray != null
		assert response.contentAsByteArray.length > 0

		Map<String, Object> result = mapper.readValue(
			response.contentAsByteArray,
			new TypeReference<Map<String, Object>>() {}
		)
		def data = result.get("data")
		assert data instanceof List: "响应JSON不是数组封装在 Result.data 中"

		return ((List<?>) data).collect { it as Map<String, Object> }
	}

	static class NoOpChain implements FilterChain {
		boolean invoked = false

		@Override
		void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			invoked = false
		}
	}

	static class CapturingChain implements FilterChain {
		boolean invoked = false
		int callCount = 0

		@Override
		void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			invoked = true
			callCount++
		}
	}
}
