/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.utils;

import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.io.utils.FilenameUtils;
import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.common.Range;
import io.github.pangju666.framework.web.model.common.Result;
import io.github.pangju666.framework.web.pool.WebConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ResponseUtils {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);
	protected static final Pattern RANGE_PATTERN = Pattern.compile("^bytes=\\d*-\\d*(,\\d*-\\d*)*$");

	protected ResponseUtils() {
	}

	/**
	 * 设置HTTP响应为附件下载，使用UTF-8字符集编码文件名
	 * <p>
	 * 通过添加Content-Disposition头，将响应设置为文件下载模式。
	 * 文件名会使用URL编码处理，确保在不同浏览器中正确显示。
	 * </p>
	 *
	 * @param response HTTP响应对象，不能为null
	 * @param filename 下载文件名，如为空则不设置
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @since 1.0.0
	 */
	public static void setAttachmentHeader(final HttpServletResponse response, @Nullable final String filename) {
		setAttachmentHeader(response, filename, StandardCharsets.UTF_8);
	}

	/**
	 * 设置HTTP响应为附件下载，使用指定字符集编码文件名
	 * <p>
	 * 通过添加Content-Disposition头，将响应设置为文件下载模式。
	 * 文件名会使用指定字符集进行URL编码处理，如未指定字符集则默认使用UTF-8。
	 * </p>
	 *
	 * @param response HTTP响应对象，不能为null
	 * @param filename 下载文件名，如为空则不设置
	 * @param charsets 文件名编码字符集，如为null则使用UTF-8
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @since 1.0.0
	 */
	public static void setAttachmentHeader(final HttpServletResponse response, @Nullable final String filename,
										   @Nullable final Charset charsets) {
		Assert.notNull(response, "response 不可为null");

		if (StringUtils.isNotBlank(filename)) {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
				URLEncoder.encode(filename, ObjectUtils.defaultIfNull(charsets, StandardCharsets.UTF_8)));
		}
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, HttpStatus.OK.value());
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status.value());
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response, final int status) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @throws IllegalArgumentException 当response为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response, final String contentType) {
		writeBytesToResponse(bytes, response, contentType, HttpStatus.OK.value());
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param status      HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response、status为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response,
											final String contentType, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, contentType, status.value());
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和状态码
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组，可以为null
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型，不能为空
	 * @param status      HTTP状态码数值
	 * @throws IllegalArgumentException 当response为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(@Nullable final byte[] bytes, final HttpServletResponse response,
											final String contentType, final int status) {
		Assert.notNull(response, "response 不可为null");
		Assert.hasText(contentType, "contentType 不可为空");

		try (OutputStream outputStream = response.getOutputStream()) {
			response.setStatus(status);
			response.setContentType(contentType);
			outputStream.write(ArrayUtils.nullToEmpty(bytes));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response或inputStream为null时抛出
	 * @throws UncheckedIOException 写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, HttpStatus.OK.value());
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response HTTP响应对象，不能为null
	 * @param status HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response、inputStream或status为null时抛出
	 * @throws UncheckedIOException 写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status.value());
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response HTTP响应对象，不能为null
	 * @param status HTTP状态码数值
	 * @throws IllegalArgumentException 当response、inputStream为null时抛出
	 * @throws UncheckedIOException 写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final int status) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @throws IllegalArgumentException 当response、inputStream或contentType为null/空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final String contentType) {
		writeInputStreamToResponse(inputStream, response, contentType, HttpStatus.OK.value());
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param status      HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response、inputStream、status或contentType为null/空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final String contentType, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, contentType, status.value());
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和状态码
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * 输入流会完整传输到响应输出流中。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流，不能为null
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型，不能为空
	 * @param status      HTTP状态码数值
	 * @throws IllegalArgumentException 当response、inputStream为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final String contentType, final int status) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(inputStream, "inputStream 不可为null");
		Assert.hasText(contentType, "contentType 不可为空");

		try (OutputStream outputStream = response.getOutputStream()) {
			response.setStatus(status);
			response.setContentType(contentType);
			inputStream.transferTo(outputStream);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * 将HTTP异常信息写入响应
	 * <p>
	 * 根据异常类型上的@HttpException注解配置，将异常信息转换为标准Result格式并写入响应。
	 * 如异常类型上没有注解，则使用默认错误码和HTTP 200状态码。
	 * 根据异常注解配置决定是否记录错误日志。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param httpException HTTP异常对象，不能为null
	 * @param response      HTTP响应对象，不能为null
	 * @param <E>           继承自BaseHttpException的异常类型
	 * @throws IllegalArgumentException 当response或httpException为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <E extends BaseHttpException> void writeHttpExceptionToResponse(final E httpException,
																				  final HttpServletResponse response) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(httpException, "httpException 不可为null");

		HttpException annotation = httpException.getClass().getAnnotation(HttpException.class);
		if (Objects.nonNull(annotation)) {
			Result<Void> result = Result.fail(annotation.type().computeCode(annotation.code()), httpException.getMessage());
			if (annotation.log()) {
				httpException.log(LOGGER, Level.ERROR);
			}
			writeResultToResponse(result, response, annotation.status().value());
		} else {
			Result<Void> result = Result.fail(WebConstants.BASE_ERROR_CODE, httpException.getMessage());
			httpException.log(LOGGER, Level.ERROR);
			writeResultToResponse(result, response, HttpStatus.OK.value());
		}
	}

	/**
	 * 将JavaBean对象以JSON格式写入响应，使用HTTP 200状态码
	 * <p>
	 * 将对象包装为{@link Result#ok(T)}并以JSON格式写入响应，状态码为HTTP 200。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param bean     要写入的JavaBean对象
	 * @param response HTTP响应对象，不能为null
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response) {
		writeResultToResponse(Result.ok(bean), response, HttpStatus.OK.value());
	}

	/**
	 * 将JavaBean对象以JSON格式写入响应，使用指定状态枚举
	 * <p>
	 * 将对象包装为{@link Result#ok(T)}并以JSON格式写入响应，使用指定HTTP状态码。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param bean 要写入的JavaBean对象
	 * @param response HTTP响应对象，不能为null
	 * @param status HTTP状态码枚举，不能为null
	 * @param <T> Bean对象类型
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException 写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeResultToResponse(Result.ok(bean), response, status.value());
	}

	/**
	 * 将JavaBean对象以JSON格式写入响应，使用指定状态码
	 * <p>
	 * 将对象包装为{@link Result#ok(T)}并以JSON格式写入响应，使用指定HTTP状态码。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param bean     要写入的JavaBean对象
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final int status) {
		writeResultToResponse(Result.ok(bean), response, status);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用HTTP 200状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result   要写入的Result对象
	 * @param response HTTP响应对象，不能为null
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response) {
		writeResultToResponse(result, response, HttpStatus.OK.value());
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态枚举
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result   要写入的Result对象
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response、result或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response,
												 final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeResultToResponse(result, response, status.value());
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result 要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param status HTTP状态码数值
	 * @param <T> Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException 写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response, final int status) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(result, "result 不可为null");

		try (OutputStream outputStream = response.getOutputStream()) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			response.setStatus(status);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			outputStream.write(result.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void writeFileToResponse(final File file, @Nullable final String responseFilename, @Nullable final String contentType,
										   final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Assert.notNull(request, "request 不可为null");
		Assert.notNull(response, "response 不可为null");
		FileUtils.checkFile(file, "file 不可为null");

		if (StringUtils.isNotBlank(responseFilename)) {
			String attachmentFilename = FilenameUtils.replaceBaseName(file.getName(), responseFilename);
			ResponseUtils.setAttachmentHeader(response, attachmentFilename);
		}
		response.setContentType(contentType);
		response.setContentLengthLong(file.length());

		String range = request.getHeader(HttpHeaders.RANGE);
		if (StringUtils.isBlank(range)) {
			try (InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file);
				 OutputStream outputStream = new BufferedOutputStream(response.getOutputStream())) {
				inputStream.transferTo(outputStream);
				outputStream.flush();
			}
		} else {
			try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
				response.setBufferSize(IOUtils.DEFAULT_BUFFER_SIZE);
				response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

				List<Range> ranges = getRanges(file, range, response);
				writeRangesToResponse(ranges, randomAccessFile, file.length(), response);
			}
		}
		response.flushBuffer();
	}

	protected static List<Range> getRanges(final File file, String rangeValue, final HttpServletResponse response) throws IOException {
		long fileLength = file.length();
		List<Range> ranges = new ArrayList<>();

		if (!RANGE_PATTERN.matcher(rangeValue).matches()) {
			response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
			response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
			return Collections.emptyList();
		}

		rangeValue = StringUtils.substringAfter(rangeValue, "bytes=");
		for (String part : rangeValue.split(",")) {
			part = part.split("/")[0];

			int delimiterIndex = part.indexOf("-");
			long start = rangePartToLong(part, 0, delimiterIndex);
			long end = rangePartToLong(part, delimiterIndex + 1, part.length());

			if (start == 0 && end == fileLength - 1) {
				Range fullRange = new Range(0, fileLength - 1, fileLength);
				//todo
				//fullRange.setFull(true);
				return Collections.singletonList(fullRange);
			}

			if (start == -1) {
				start = fileLength - end;
				end = fileLength - 1;
			} else if (end == -1 || end > fileLength - 1) {
				end = fileLength - 1;
			}

			if (start > end) {
				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
				response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
				return Collections.emptyList();
			}

			ranges.add(new Range(start, end, end - start + 1));
		}
		return ranges;
	}

	protected static void writeRangesToResponse(final List<Range> ranges, final RandomAccessFile randomAccessFile,
												final long length, final HttpServletResponse response) throws IOException {
		try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
			if (ranges.size() <= 1) {
				Range range = ranges.get(0);

				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());
				response.setContentLengthLong(range.getLength());
				if (!range.isFull()) {
					response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				}

				writeFileToOutputStream(randomAccessFile, response.getOutputStream(), length, range.getStart(), range.getLength());
				servletOutputStream.flush();
			} else {
				// 返回文件的多个分段.
				response.setContentType("multipart/byteranges; boundary=MULTIPART_BYTERANGES");
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

				// 复制多个文件分段.
				for (Range range : ranges) {
					//为每个Range添加MULTIPART边界和标题字段
					servletOutputStream.println();
					servletOutputStream.println("--MULTIPART_BYTERANGES");
					servletOutputStream.println(HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_OCTET_STREAM_VALUE);
					servletOutputStream.println(HttpHeaders.CONTENT_LENGTH + ": " + range.getLength());
					servletOutputStream.println(HttpHeaders.CONTENT_RANGE + ": bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());

					// 复制多个需要复制的文件分段当中的一个分段.
					writeFileToOutputStream(randomAccessFile, response.getOutputStream(), length, range.getStart(), range.getLength());
				}

				servletOutputStream.println();
				servletOutputStream.println("--MULTIPART_BYTERANGES--");
				servletOutputStream.flush();
			}
		}
	}

	protected static void writeFileToOutputStream(final RandomAccessFile randomAccessFile, final OutputStream output,
												  final long fileSize, final long start, final long length) throws IOException {
		byte[] buffer = new byte[4096];
		int read = 0;
		long transmitted = 0;
		if (fileSize == length) {
			randomAccessFile.seek(start);
			//需要下载的文件长度与文件长度相同，下载整个文件.
			while ((transmitted + read) <= length && (read = randomAccessFile.read(buffer)) != -1) {
				output.write(buffer, 0, read);
				transmitted += read;
			}
			//处理最后不足buff大小的部分
			if (transmitted < length) {
				read = randomAccessFile.read(buffer, 0, (int) (length - transmitted));
				output.write(buffer, 0, read);
			}
		} else {
			randomAccessFile.seek(start);
			long toRead = length;

			//如果需要读取的片段，比单次读取的4096小，则使用读取片段大小读取
			if (toRead < buffer.length) {
				output.write(buffer, 0, randomAccessFile.read(new byte[(int) toRead]));
				return;
			}
			while ((read = randomAccessFile.read(buffer)) > 0) {
				toRead -= read;
				if (toRead > 0) {
					output.write(buffer, 0, read);
				} else {
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	protected static Long rangePartToLong(final String part, final int beginIndex, final int endIndex) {
		String substring = part.substring(beginIndex, endIndex);
		return (!substring.isEmpty()) ? Long.parseLong(substring) : -1;
	}
}