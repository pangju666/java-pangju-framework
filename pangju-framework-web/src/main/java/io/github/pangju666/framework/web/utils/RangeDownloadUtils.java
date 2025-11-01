package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.commons.lang.utils.RegExUtils;
import io.github.pangju666.framework.web.model.common.Range;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * HTTP 分片下载工具类
 * <p>
 * 提供支持 HTTP Range 请求的文件和字节数组下载功能：
 * <ul>
 *     <li>支持单一范围和多范围请求</li>
 *     <li>支持文件和字节数组作为数据源</li>
 *     <li>符合 HTTP/1.1 规范中的范围请求处理标准</li>
 *     <li>自动解析和验证 Range 请求头</li>
 *     <li>提供合适的状态码和响应头处理</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>大文件下载时需要支持断点续传</li>
 *     <li>视频流媒体播放时需要支持拖动进度条</li>
 *     <li>需要支持分片下载的场景</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see Range
 * @since 1.0.0
 */
public class RangeDownloadUtils {
	/**
	 * 范围请求头模式匹配器
	 * <p>
	 * 用于解析HTTP Range请求头，匹配形如"bytes=0-1024,2048-3072"的范围请求格式。
	 * 支持多个范围段的请求格式解析。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d+-\\d*(, ?\\d+-\\d*)*$");
	/**
	 * 多范围请求的分隔符
	 * <p>
	 * 用于分割 Range 请求头中的多个范围值，例如 "bytes=0-100,200-300" 中的逗号
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RANGES_DELIMITER = ",";
	/**
	 * 范围起止值分隔符
	 * <p>
	 * 用于分割 Range 单个范围值中的起始和结束位置，例如 "0-100" 中的连字符
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RANGE_DELIMITER = "-";
	/**
	 * 请求范围头值前缀
	 * <p>
	 * HTTP Range 请求头的值前缀，通常为 "bytes="
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String REQUEST_RANGE_HEADER_VALUE_PREFIX = "bytes=";
	/**
	 * 响应接受范围头值
	 * <p>
	 * Accept-Ranges 响应头的值，表示服务器接受的范围单位
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_ACCEPT_RANGES_HEADER_VALUE = "bytes";
	/**
	 * 响应内容范围头前缀
	 * <p>
	 * Content-Range 响应头的前缀，用于表示不满足范围请求时的资源总大小
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_CONTENT_RANGE_HEADER_PREFIX = "bytes */";
	/**
	 * 响应多范围内容分隔符
	 * <p>
	 * 用于 multipart/byteranges 响应类型中分隔多个部分内容的边界标识符
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_RANGES_CONTENT_DELIMITER = "MULTIPART_BYTERANGES";
	/**
	 * 响应多范围内容类型
	 * <p>
	 * 当响应包含多个范围时使用的 Content-Type 值，包含 boundary 参数
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_RANGES_CONTENT_TYPE = "multipart/byteranges; boundary=" + RESPONSE_RANGES_CONTENT_DELIMITER;
	/**
	 * 响应内容范围格式
	 * <p>
	 * Content-Range 响应头的格式字符串，包含起始位置、结束位置和总大小
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_CONTENT_RANGE_FORMAT = "bytes %d-%d/%d";
	/**
	 * 响应多范围内容开始标记
	 * <p>
	 * multipart/byteranges 响应中每个部分的开始边界标记
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_CONTENT_RANGE_START_FLAG = "--" + RESPONSE_RANGES_CONTENT_DELIMITER;
	/**
	 * 响应多范围内容结束标记
	 * <p>
	 * multipart/byteranges 响应的结束边界标记
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String RESPONSE_CONTENT_RANGE_END_FLAG = "--" + RESPONSE_RANGES_CONTENT_DELIMITER + "--";
	/**
	 * HTTP 响应中的换行符
	 * <p>
	 * 符合 HTTP 规范的 CRLF 换行符
	 * </p>
	 *
	 * @since 1.0.0
	 */

	public static final String NEW_LINE = "\r\n";

	protected RangeDownloadUtils() {
	}

	/**
	 * 支持范围下载的字节数组下载处理
	 * <p>
	 * 处理流程：
	 * <ol>
	 *     <li>检查请求中是否包含 Range 头</li>
	 *     <li>如果没有 Range 头，则返回完整字节数组</li>
	 *     <li>如果有 Range 头，解析请求的范围</li>
	 *     <li>验证请求范围的有效性</li>
	 *     <li>返回请求范围的字节数据</li>
	 * </ol>
	 * </p>
	 *
	 * @param bytes    要下载的字节数组
	 * @param request  HTTP 请求对象，用于获取 Range 头
	 * @param response HTTP 响应对象，用于设置状态码和响应头
	 * @throws IOException              IO异常
	 * @throws IllegalArgumentException 如果参数为空
	 * @since 1.0.0
	 */
	public static void downloadBytes(final byte[] bytes, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(rangeHeader)) {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setContentLengthLong(bytes.length);
			HttpServletResponseUtils.writeBytesToResponse(bytes, response, true);
		} else {
			List<Range> ranges = getRanges(bytes.length, rangeHeader);

			// 超出字节数组总长度或格式错误
			if (Objects.isNull(ranges)) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_HEADER_PREFIX + bytes.length);
				response.setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return;
			}

			// 返回完整内容
			if (ranges.size() == 1 && ranges.get(0).isComplete()) {
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				response.setContentLengthLong(bytes.length);
				HttpServletResponseUtils.writeBytesToResponse(bytes, response, true);
				return;
			}

			response.setHeader(HttpHeaders.ACCEPT_RANGES, RESPONSE_ACCEPT_RANGES_HEADER_VALUE);
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			writeRangesToResponse(ranges, bytes, response);
		}
	}

	/**
	 * 支持范围下载的文件下载处理
	 * <p>
	 * 处理流程：
	 * <ol>
	 *     <li>检查请求中是否包含 Range 头</li>
	 *     <li>如果没有 Range 头，则返回完整文件内容</li>
	 *     <li>如果有 Range 头，解析请求的范围</li>
	 *     <li>验证请求范围的有效性</li>
	 *     <li>返回请求范围的文件数据</li>
	 * </ol>
	 * </p>
	 *
	 * @param file     要下载的文件
	 * @param request  HTTP 请求对象，用于获取 Range 头
	 * @param response HTTP 响应对象，用于设置状态码和响应头
	 * @throws IOException              IO异常
	 * @throws IllegalArgumentException 如果参数为空
	 * @since 1.0.0
	 */
	public static void downloadFile(final File file, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(rangeHeader)) {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setContentLengthLong(file.length());
			HttpServletResponseUtils.writeFileToResponse(file, response, null, null, true);
		} else {
			long fileLength = file.length();
			List<Range> ranges = getRanges(fileLength, rangeHeader);

			// 超出字节数组总长度或格式错误
			if (Objects.isNull(ranges)) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_HEADER_PREFIX + fileLength);
				response.setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return;
			}

			// 返回完整内容
			if (ranges.size() == 1 && ranges.get(0).isComplete()) {
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				response.setContentLengthLong(fileLength);
				HttpServletResponseUtils.writeFileToResponse(file, response, null, null, true);
				return;
			}

			response.setHeader(HttpHeaders.ACCEPT_RANGES, RESPONSE_ACCEPT_RANGES_HEADER_VALUE);
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			writeRangesToResponse(ranges, file, response);
		}
	}

	/**
	 * 解析和验证范围请求
	 * <p>
	 * 从Range请求头中解析出所有请求的范围，并对以下情况进行验证：
	 * <ul>
	 *     <li>Range格式是否符合规范</li>
	 *     <li>请求范围是否超出资源总长度</li>
	 *     <li>范围的起始位置是否大于结束位置</li>
	 * </ul>
	 * </p>
	 *
	 * @param totalLength 资源总长度
	 * @param range       Range请求头值
	 * @return 解析后的范围列表，如果范围无效则返回null
	 * @since 1.0.0
	 */
	public static List<Range> getRanges(final long totalLength, @Nullable final String range) {
		if (!RegExUtils.matches(RANGE_PATTERN, range)) {
			return null;
		}

		List<Range> ranges = new ArrayList<>();
		String rangeValue = StringUtils.substringAfter(range, REQUEST_RANGE_HEADER_VALUE_PREFIX);

		for (String part : rangeValue.split(RANGES_DELIMITER)) {
			String[] partRange = part.trim().split(RANGE_DELIMITER);
			long start = Long.parseLong(partRange[0]);

			if (partRange.length == 1) {
				if (start == 0) {
					ranges.add(Range.complete(totalLength));
				} else if (start > totalLength - 1) {
					return null;
				} else {
					ranges.add(new Range(start, totalLength - start - 1, totalLength));
				}
			} else {
				long end = Long.parseLong(partRange[1]);

				if (start == 0 && end == totalLength - 1) {
					return Collections.singletonList(Range.complete(totalLength));
				} else if (start > totalLength - 1 || end > totalLength - 1 || start > end) {
					return null;
				} else {
					ranges.add(new Range(start, end, totalLength));
				}
			}
		}
		return ranges;
	}

	/**
	 * 将文件范围内容写入响应
	 * <p>
	 * 根据请求的范围列表，将文件中对应范围的内容写入HTTP响应：
	 * <ul>
	 *     <li>对于单一范围请求，直接返回对应范围的内容</li>
	 *     <li>对于多范围请求，返回multipart/byteranges格式的内容</li>
	 * </ul>
	 * </p>
	 *
	 * @param ranges   请求的范围列表
	 * @param file     源文件
	 * @param response HTTP响应对象
	 * @throws IOException IO异常
	 * @since 1.0.0
	 */
	public static void writeRangesToResponse(final List<Range> ranges, final File file,
											 final HttpServletResponse response) throws IOException {
		if (ranges.size() == 1) {
			Range range = ranges.get(0);

			response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_FORMAT.formatted(
				range.getStart(), range.getEnd(), range.getTotal()));
			response.setContentLengthLong(range.getLength());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream());
				 InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file)) {
				long skipBytes = 0;
				while (skipBytes != range.getStart()) {
					skipBytes += inputStream.skip(range.getStart() - skipBytes);
				}
				byte[] bytes = inputStream.readNBytes((int) range.getLength());
				outputStream.write(bytes);
			}
		} else {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream());
				 RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				// 返回文件的多个分段.
				response.setContentType(RESPONSE_RANGES_CONTENT_TYPE);
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				for (Range range : ranges) {
					writeRangeHeaders(range, outputStream);
					randomAccessFile.seek(range.getStart());
					byte[] buffer = new byte[(int) (range.getEnd() - range.getStart() + 1)];
					randomAccessFile.readFully(buffer);
					outputStream.write(buffer);
					outputStream.write(NEW_LINE.getBytes());
				}
				outputStream.write((RESPONSE_CONTENT_RANGE_END_FLAG).getBytes());
			}
		}
	}

	/**
	 * 将字节数组范围内容写入响应
	 * <p>
	 * 根据请求的范围列表，将字节数组中对应范围的内容写入HTTP响应：
	 * <ul>
	 *     <li>对于单一范围请求，直接返回对应范围的内容</li>
	 *     <li>对于多范围请求，返回multipart/byteranges格式的内容</li>
	 * </ul>
	 * </p>
	 *
	 * @param ranges   请求的范围列表
	 * @param bytes    源字节数组
	 * @param response HTTP响应对象
	 * @throws IOException IO异常
	 * @since 1.0.0
	 */
	public static void writeRangesToResponse(final List<Range> ranges, final byte[] bytes,
											 final HttpServletResponse response) throws IOException {
		if (ranges.size() == 1) {
			Range range = ranges.get(0);

			response.setHeader(HttpHeaders.CONTENT_RANGE, RESPONSE_CONTENT_RANGE_FORMAT.formatted(
				range.getStart(), range.getEnd(), range.getTotal()));
			response.setContentLengthLong(range.getLength());
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream())) {
				outputStream.write(ArrayUtils.subarray(bytes, (int) range.getStart(),
					(int) range.getEnd() + 1));
			}
		} else {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream())) {
				// 返回文件的多个分段.
				response.setContentType(RESPONSE_RANGES_CONTENT_TYPE);
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				for (Range range : ranges) {
					writeRangeHeaders(range, outputStream);
					outputStream.write(ArrayUtils.subarray(bytes, (int) range.getStart(),
						(int) range.getEnd() + 1));
					outputStream.write(NEW_LINE.getBytes());
				}
				outputStream.write((RESPONSE_CONTENT_RANGE_END_FLAG).getBytes());
			}
		}
	}

	/**
	 * 写入多范围响应的范围头信息
	 * <p>
	 * 为multipart/byteranges格式的响应中的每个部分写入必要的头信息：
	 * <ul>
	 *     <li>Range标识分隔符</li>
	 *     <li>Content-Type头</li>
	 *     <li>Content-Range头</li>
	 *     <li>必要的空行</li>
	 * </ul>
	 * </p>
	 *
	 * @param range        单个范围信息
	 * @param outputStream 输出流
	 * @throws IOException IO异常
	 * @since 1.0.0
	 */
	public static void writeRangeHeaders(Range range, OutputStream outputStream) throws IOException {
		//为每个Range添加MULTIPART边界和标题字段
		outputStream.write((RESPONSE_CONTENT_RANGE_START_FLAG + NEW_LINE).getBytes());
		outputStream.write((HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_OCTET_STREAM_VALUE
			+ NEW_LINE).getBytes());
		outputStream.write((HttpHeaders.CONTENT_LENGTH + ": " + range.getLength() + NEW_LINE).getBytes());
		outputStream.write((HttpHeaders.CONTENT_RANGE + ": " + RESPONSE_CONTENT_RANGE_FORMAT.formatted(
			range.getStart(), range.getEnd(), range.getTotal()) + NEW_LINE).getBytes());
		outputStream.write(NEW_LINE.getBytes());
	}
}
