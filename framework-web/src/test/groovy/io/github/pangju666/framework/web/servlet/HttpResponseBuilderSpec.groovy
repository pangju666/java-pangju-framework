package io.github.pangju666.framework.web.servlet

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.pangju666.commons.io.utils.FileUtils
import io.github.pangju666.framework.web.enums.HttpExceptionType
import io.github.pangju666.framework.web.exception.base.BaseHttpException
import io.github.pangju666.framework.web.exception.base.ServiceException
import io.github.pangju666.framework.web.lang.WebConstants
import io.github.pangju666.framework.web.model.Result
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = Config)
class HttpResponseBuilderSpec extends Specification {
	@SpringBootConfiguration
	@EnableAutoConfiguration
	static class Config {
		// 最小Spring Boot上下文，无需Bean
	}

	private final ObjectMapper mapper = new ObjectMapper()

	def "from 工厂方法返回构建器实例且默认缓冲启用"() {
		given:
		def response = new MockHttpServletResponse()

		when:
		def builder = HttpResponseBuilder.from(response)

		then:
		builder != null
		// 无直接API获取buffer状态，后续通过行为用例覆盖；此处只校验实例构建成功
	}

	def "write(InputStream) 默认设置Content-Type并保留容器默认字符集，且写出全部内容"() {
		given:
		byte[] data = "hello world".getBytes("UTF-8")
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.write(new ByteArrayInputStream(data))

		then:
		response.getContentType() == MediaType.APPLICATION_OCTET_STREAM_VALUE
		// 保留容器默认字符集（Mock 默认 ISO-8859-1）
		response.getCharacterEncoding() == "ISO-8859-1"
		response.getContentAsByteArray() == data
	}

	def "write(byte[]) 设置Content-Length并写出字节数组"() {
		given:
		byte[] data = [1, 2, 3, 4, 5] as byte[]
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.write(data)

		then:
		response.getContentType() == MediaType.APPLICATION_OCTET_STREAM_VALUE
		// 保留容器默认字符集（Mock 默认 ISO-8859-1）
		response.getCharacterEncoding() == "ISO-8859-1"
		response.getContentLength() == data.length
		response.getContentAsByteArray() == data
	}

	def "writeFile(File) 设置Content-Type、Content-Length与Content-Disposition，并写出内容"() {
		given:
		File temp = File.createTempFile("hrb-spec-", ".txt")
		temp.deleteOnExit()
		temp.write("abc123", "UTF-8")

		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.writeFile(temp)

		then:
		response.getContentType() == FileUtils.getMimeType(temp)
		response.getContentLength() == (int) temp.length()
		response.getHeader(HttpHeaders.CONTENT_DISPOSITION).startsWith("attachment;filename=")
		new String(response.getContentAsByteArray(), "UTF-8") == "abc123"
	}

	def "writeFile(File, filename) 使用指定文件名设置Content-Disposition"() {
		given:
		File temp = File.createTempFile("hrb-spec-", ".bin")
		temp.deleteOnExit()
		temp.bytes = [9, 8, 7] as byte[]

		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.writeFile(temp, "测试文件.bin")

		then:
		response.getContentType() == FileUtils.getMimeType(temp)
		response.getContentLength() == (int) temp.length()
		response.getHeader(HttpHeaders.CONTENT_DISPOSITION).startsWith("attachment;filename=")
		// filename 会被URL编码，这里校验包含编码后的前缀即可
		!response.getHeader(HttpHeaders.CONTENT_DISPOSITION).contains("测试文件.bin".bytes.encodeBase64().toString()) // 不应是Base64
		response.getHeader(HttpHeaders.CONTENT_DISPOSITION).contains("%E6%B5%8B%E8%AF%95") // URL编码片段存在
		response.getContentAsByteArray() == temp.bytes
	}

	def "contentDisposition 设置下载响应头（默认UTF-8编码）"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.contentDisposition("中文 文件.txt")

		then:
		def cd = response.getHeader(HttpHeaders.CONTENT_DISPOSITION)
		cd.startsWith("attachment;filename=")
		cd.contains("%E4%B8%AD%E6%96%87") // URL编码片段
	}

	def "characterEncoding 与 contentType 可显式设置"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.contentType(MediaType.TEXT_PLAIN_VALUE).characterEncoding(StandardCharsets.UTF_8).write("x".bytes)

		then:
		// Content-Type 包含 charset，使用前缀断言或 MediaType 比较
		response.getContentType().startsWith(MediaType.TEXT_PLAIN_VALUE)
		response.getCharacterEncoding() == StandardCharsets.UTF_8.toString()
		response.getContentAsByteArray() == "x".bytes
	}

	def "cacheControl(Duration) 设置秒数字符串到Cache-Control头"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.cacheControl(Duration.ofSeconds(120))

		then:
		response.getHeader(HttpHeaders.CACHE_CONTROL) == "120"
	}

	def "cacheControl(CacheControl) 设置标准化Cache-Control头值"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)

		when:
		builder.cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())

		then:
		response.getHeader(HttpHeaders.CACHE_CONTROL).contains("max-age=60")
	}

	def "writeBean(bean) 将对象包装为Result并写出JSON"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)
		def payload = [a: 1, b: "x"]

		when:
		builder.writeBean(payload)

		then:
		response.getContentType() == MediaType.APPLICATION_JSON_VALUE

		and:
		Map<String, Object> result = mapper.readValue(response.getContentAsByteArray(), new TypeReference<Map<String, Object>>() {
		})
		result.code == WebConstants.SUCCESS_CODE
		result.message == WebConstants.DEFAULT_SUCCESS_MESSAGE
		result.data instanceof Map
		((Map) result.data).a == 1
		((Map) result.data).b == "x"
	}

	def "writeBean(Result) 直接写出已有的Result对象"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)
		def res = Result.ok([k: "v"])

		when:
		builder.writeBean(res)

		then:
		response.getContentType() == MediaType.APPLICATION_JSON_VALUE

		and:
		Map<String, Object> result = mapper.readValue(response.getContentAsByteArray(), new TypeReference<Map<String, Object>>() {
		})
		result.code == WebConstants.SUCCESS_CODE
		((Map) result.data).k == "v"
	}

	def "writeHttpException(ServiceException) 使用注解设置HTTP状态与业务错误码"() {
		given:
		def response = new MockHttpServletResponse()
		def builder = HttpResponseBuilder.from(response)
		BaseHttpException ex = new ServiceException("业务错误")

		when:
		builder.writeHttpException(ex)

		then:
		// ServiceException 的 @HttpException 默认 status=OK
		response.getStatus() == HttpStatus.OK.value()
		response.getContentType() == MediaType.APPLICATION_JSON_VALUE

		and:
		Map<String, Object> result = mapper.readValue(response.getContentAsByteArray(), new TypeReference<Map<String, Object>>() {
		})
		// code = type.computeCode(code)，ServiceException 注解 code=0，type=SERVICE
		result.code == HttpExceptionType.SERVICE.computeCode(0)
		result.message == "业务错误"
		result.data == null
	}

	def "status(int) 与 status(HttpStatus) 可显式设置响应码"() {
		given:
		def response1 = new MockHttpServletResponse()
		def builder1 = HttpResponseBuilder.from(response1)

		when:
		builder1.status(HttpStatus.CREATED).write("ok".bytes)

		then:
		response1.getStatus() == HttpStatus.CREATED.value()

		when:
		// 使用新的响应对象，避免已提交响应导致状态码变更被忽略
		def response2 = new MockHttpServletResponse()
		def builder2 = HttpResponseBuilder.from(response2)
		builder2.status(204)

		then:
		response2.getStatus() == 204
	}
}
