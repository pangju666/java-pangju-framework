package io.github.pangju666.framework.web

import io.github.pangju666.commons.io.utils.FileUtils
import io.github.pangju666.framework.web.model.Range
import io.github.pangju666.framework.web.servlet.utils.RangeDownloadUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Unroll

class RangeDownloadUtilsSpec extends Specification {
	// ... existing code ...
	def "getRanges - 无效格式返回null"() {
		expect:
		RangeDownloadUtils.getRanges(100, null) == null
		RangeDownloadUtils.getRanges(100, "") == null
		RangeDownloadUtils.getRanges(100, "bytes=") == null
		RangeDownloadUtils.getRanges(100, "items=0-1") == null
		RangeDownloadUtils.getRanges(100, "bytes=a-b") == null
	}

	// ... existing code ...
	def "getRanges - 单段闭区间解析正确"() {
		given:
		long total = 10
		String header = "bytes=0-9"

		when:
		def ranges = RangeDownloadUtils.getRanges(total, header)

		then:
		ranges != null
		ranges.size() == 1
		with(ranges[0]) {
			getStart() == 0
			getEnd() == 9
			getLength() == 10
			getTotal() == total
			isComplete()
		}
	}

	// ... existing code ...
	def "getRanges - 单段完整内容闭区间解析为complete"() {
		given:
		long total = 123
		String header = "bytes=0-${total - 1}"

		when:
		def ranges = RangeDownloadUtils.getRanges(total, header)

		then:
		ranges != null
		ranges.size() == 1
		ranges[0].isComplete()
		ranges[0].getStart() == 0
		ranges[0].getEnd() == total - 1
		ranges[0].getTotal() == total
		ranges[0].getLength() == total
	}

	// ... existing code ...
	def "getRanges - 多段闭区间解析正确"() {
		given:
		long total = 50
		String header = "bytes=0-9, 20-29,30-39"

		when:
		def ranges = RangeDownloadUtils.getRanges(total, header)

		then:
		ranges != null
		ranges.size() == 3
		with(ranges[0]) {
			getStart() == 0
			getEnd() == 9
			getLength() == 10
			getTotal() == total
		}
		with(ranges[1]) {
			getStart() == 20
			getEnd() == 29
			getLength() == 10
			getTotal() == total
		}
		with(ranges[2]) {
			getStart() == 30
			getEnd() == 39
			getLength() == 10
			getTotal() == total
		}
	}

	// ... existing code ...
	def "getRanges - 起始为0的起始到末尾格式解析为complete"() {
		given:
		long total = 77
		String header = "bytes=0-"

		when:
		def ranges = RangeDownloadUtils.getRanges(total, header)

		then:
		ranges != null
		ranges.size() == 1
		ranges[0].isComplete()
		ranges[0].getStart() == 0
		ranges[0].getEnd() == total - 1
		ranges[0].getLength() == total
		ranges[0].getTotal() == total
	}

	// ... existing code ...
	def "getRanges - 越界或非法范围返回null"() {
		given:
		long total = 10

		expect:
		RangeDownloadUtils.getRanges(total, "bytes=11-12") == null  // start > total-1
		RangeDownloadUtils.getRanges(total, "bytes=0-10") == null   // end > total-1
		RangeDownloadUtils.getRanges(total, "bytes=9-2") == null    // start > end
		RangeDownloadUtils.getRanges(total, "bytes=1-5, 20-29") == null // 第二段越界（避免首段是完整内容）
	}

	// ... existing code ...
	def "download(byte[]) - 无Range头返回完整内容，200，application/octet-stream"() {
		given:
		byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".bytes
		def req = new MockHttpServletRequest()
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(bytes, req, resp)

		then:
		resp.status == HttpStatus.OK.value()
		resp.getHeader(HttpHeaders.ACCEPT_RANGES) == null
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == null
		resp.getContentType() == MediaType.APPLICATION_OCTET_STREAM_VALUE
		resp.getContentAsByteArray() == bytes
	}

	// ... existing code ...
	def "download(byte[]) - 单段范围返回206、设置Accept-Ranges与Content-Range与内容长度"() {
		given:
		byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".bytes
		def req = new MockHttpServletRequest()
		req.addHeader(HttpHeaders.RANGE, "bytes=5-9") // F..J
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(bytes, req, resp)

		then:
		resp.status == HttpStatus.PARTIAL_CONTENT.value()
		resp.getHeader(HttpHeaders.ACCEPT_RANGES) == "bytes"
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == "bytes 5-9/${bytes.length}"
		resp.getContentType() == MediaType.APPLICATION_OCTET_STREAM_VALUE
		resp.contentLength == 5
		new String(resp.getContentAsByteArray()) == "FGHIJ"
	}

	// ... existing code ...
	def "download(byte[]) - 多段范围返回multipart/byteranges，含分段头与结束边界"() {
		given:
		byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".bytes
		def req = new MockHttpServletRequest()
		req.addHeader(HttpHeaders.RANGE, "bytes=0-4, 10-14") // A..E and K..O
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(bytes, req, resp)
		def body = resp.getContentAsByteArray()
		def bodyStr = new String(body) // 仅用于包含校验；内容段是ASCII，安全

		then:
		resp.status == HttpStatus.PARTIAL_CONTENT.value()
		resp.getHeader(HttpHeaders.ACCEPT_RANGES) == "bytes"
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == null // 多段时不在主响应头
		resp.getContentType() == "multipart/byteranges; boundary=MULTIPART_BYTERANGES"

		// 每段都应包含边界与头
		bodyStr.contains("--MULTIPART_BYTERANGES")
		bodyStr.contains("${HttpHeaders.CONTENT_TYPE}: ${MediaType.APPLICATION_OCTET_STREAM_VALUE}")
		bodyStr.contains("${HttpHeaders.CONTENT_RANGE}: bytes 0-4/${bytes.length}")
		bodyStr.contains("${HttpHeaders.CONTENT_RANGE}: bytes 10-14/${bytes.length}")

		// 内容包含两个范围对应的段
		bodyStr.contains("ABCDE")
		bodyStr.contains("KLMNO")

		// 结束边界
		bodyStr.endsWith("--MULTIPART_BYTERANGES--")
	}

	// ... existing code ...
	def "download(File) - 无Range头返回完整文件内容，200"() {
		given:
		File tmp = File.createTempFile("rdut", ".bin")
		tmp.deleteOnExit()
		tmp.bytes = "Hello-World-File-Content".bytes

		def req = new MockHttpServletRequest()
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(tmp, req, resp)

		then:
		resp.status == HttpStatus.OK.value()
		resp.getHeader(HttpHeaders.ACCEPT_RANGES) == null
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == null
		// 使用与生产逻辑一致的 MIME 检测
		resp.getContentType() == FileUtils.getMimeType(tmp)
		// Content-Disposition 包含原文件名（ASCII 情况下 URL 编码不改变值）
		resp.getHeader(HttpHeaders.CONTENT_DISPOSITION) == "attachment;filename=${tmp.name}"
		new String(resp.getContentAsByteArray()) == "Hello-World-File-Content"
	}

	// ... existing code ...
	def "download(File) - 单段范围返回206、设置Accept-Ranges与Content-Range"() {
		given:
		File tmp = File.createTempFile("rdut", ".bin")
		tmp.deleteOnExit()
		tmp.bytes = "0123456789ABCDEFGHIJ".bytes  // total=20
		def req = new MockHttpServletRequest()
		req.addHeader(HttpHeaders.RANGE, "bytes=2-5") // '2345'
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(tmp, req, resp)

		then:
		resp.status == HttpStatus.PARTIAL_CONTENT.value()
		resp.getHeader(HttpHeaders.ACCEPT_RANGES) == "bytes"
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == "bytes 2-5/${tmp.length()}"
		resp.getContentType() == MediaType.APPLICATION_OCTET_STREAM_VALUE
		resp.contentLength == 4
		new String(resp.getContentAsByteArray()) == "2345"
	}

	// ... existing code ...
	def "download(byte[]) - 非法范围返回416与Content-Range提示总长度"() {
		given:
		byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".bytes
		def req = new MockHttpServletRequest()
		req.addHeader(HttpHeaders.RANGE, "bytes=100-200") // 越界
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(bytes, req, resp)

		then:
		resp.status == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == "bytes */${bytes.length}"
		resp.getContentAsByteArray().length == 0
	}

	// ... existing code ...
	def "download(File) - 非法范围返回416与Content-Range提示总长度"() {
		given:
		File tmp = File.createTempFile("rdut", ".bin")
		tmp.deleteOnExit()
		tmp.bytes = "0123456789".bytes // total = 10
		def req = new MockHttpServletRequest()
		req.addHeader(HttpHeaders.RANGE, "bytes=20-30") // 越界
		def resp = new MockHttpServletResponse()

		when:
		RangeDownloadUtils.download(tmp, req, resp)

		then:
		resp.status == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()
		resp.getHeader(HttpHeaders.CONTENT_RANGE) == "bytes */${tmp.length()}"
		resp.getContentAsByteArray().length == 0
	}

	// ... existing code ...
	@Unroll
	def "writeRangeHeaders - 分段头格式正确 [start:#start end:#end total:#total length:#length]"() {
		given:
		def range = new Range(start, end, total)
		def out = new ByteArrayOutputStream()

		when:
		RangeDownloadUtils.writeRangeHeaders(range, out)
		def headerStr = new String(out.toByteArray())
		def nl = RangeDownloadUtils.NEW_LINE

		then:
		headerStr == "--MULTIPART_BYTERANGES${nl}" +
			"Content-Type: ${MediaType.APPLICATION_OCTET_STREAM_VALUE}${nl}" +
			"Content-Length: ${length}${nl}" +
			"Content-Range: bytes ${start}-${end}/${total}${nl}" +
			nl

		where:
		start | end | total | length
		0     | 4   | 10    | 5
		5     | 9   | 20    | 5
	}
}
