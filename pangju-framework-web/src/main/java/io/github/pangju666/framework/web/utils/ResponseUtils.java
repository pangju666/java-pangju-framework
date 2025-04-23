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
import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.common.Result;
import io.github.pangju666.framework.web.pool.WebConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.input.BufferedFileChannelInputStream;
import org.apache.commons.io.input.UnsynchronizedBufferedInputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
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
import java.util.Objects;

/**
 * HTTP响应工具类
 * <p>
 * 提供对HTTP响应的核心操作工具集，简化Web应用中的响应处理任务。本工具类主要功能包括：
 * <ul>
 *     <li>响应内容输出：支持字节数组和输入流等数据源直接写入响应</li>
 *     <li>文件下载：设置适当的响应头实现文件附件下载</li>
 *     <li>内容类型管理：支持多种媒体类型的响应设置</li>
 *     <li>JSON处理：将Java对象、Result包装对象转换为JSON并写入响应</li>
 *     <li>异常处理：将HTTP异常信息标准化输出到客户端</li>
 *     <li>响应缓冲控制：支持配置是否使用响应缓冲区，优化性能</li>
 * </ul>
 * </p>
 *
 * <p>
 * 设计特点：
 * <ul>
 *     <li>严格的参数校验，确保API使用安全</li>
 *     <li>支持多种HTTP状态码和内容类型设置</li>
 *     <li>采用流式处理，提升大数据量传输效率</li>
 *     <li>自动资源管理，所有流操作安全关闭</li>
 *     <li>方法重载丰富，提供灵活的参数组合选项</li>
 *     <li>与Spring框架和Jakarta EE无缝集成</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 1. 设置文件下载响应头
 * setAttachmentHeader(response, "report.pdf");
 *
 * // 2. 将字节数组写入响应
 * byte[] data = generatePdfData();
 * writeBytesToResponse(data, response, "application/pdf");
 *
 * // 3. 将Java对象转为JSON响应
 * User user = userService.getCurrentUser();
 * writeBeanToResponse(user, response);
 *
 * // 4. 处理异常并输出到响应
 * try {
 *     // 业务逻辑
 * } catch (ResourceNotFoundException e) {
 *     writeHttpExceptionToResponse(e, response);
 * }
 *
 * // 5. 从输入流写入响应
 * try (InputStream fileStream = new FileInputStream(file)) {
 *     writeInputStreamToResponse(fileStream, response, "image/jpeg");
 * }
 *
 * // 6. 控制响应缓冲
 * writeBytesToResponse(largeData, response, "application/octet-stream", false);
 * }</pre>
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see jakarta.servlet.http.HttpServletResponse
 * @see org.springframework.http.HttpStatus
 * @see org.springframework.http.MediaType
 * @see FileResponseUtils
 * @since 1.0.0
 */
public class ResponseUtils {
	/**
	 * 类日志记录器实例
	 * <p>
	 * 用于记录工具类内部操作日志，主要记录异常和错误信息。
	 * </p>
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtils.class);

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
	 * 设置下载所需的HTTP响应头
	 * <p>
	 * 为HTTP响应设置必要的下载头信息，包括：
	 * <ul>
	 *     <li>Content-Disposition头：当提供文件名时设置为附件下载模式</li>
	 *     <li>Content-Type头：指定下载内容的MIME类型，默认为二进制流</li>
	 *     <li>Content-Length头：指定下载内容的字节长度</li>
	 * </ul>
	 * 此方法确保了正确设置所有必要的HTTP头，使浏览器能够正确处理下载请求。
	 * </p>
	 *
	 * @param contentLength 下载内容的字节长度，必须大于等于0
	 * @param filename      要下载的文件名，可以为null；为null时不设置附件头
	 * @param contentType   下载内容类型，可以为null；为null时使用application/octet-stream
	 * @param response      HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response为null或contentLength小于0时抛出
	 * @since 1.0.0
	 */
	public static void setDownloadHeaders(final long contentLength, @Nullable final String filename,
											 @Nullable final String contentType, final HttpServletResponse response) {
		Assert.notNull(response, "response 不可为null");
		Assert.isTrue(contentLength >= 0, "contentLength 必须大于等于0");

		if (StringUtils.isNotBlank(filename)) {
			setAttachmentHeader(response, filename);
		}
		response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
		response.setContentLengthLong(contentLength);
	}

	/**
	 * 将文件写入HTTP响应
	 * <p>
	 * 读取指定文件内容并写入HTTP响应流，自动设置适当的下载头信息。
	 * 默认使用缓冲流提高传输性能。
	 * </p>
	 *
	 * @param file             要写入的文件，不能为null
	 * @param downloadFilename 下载时显示的文件名，如为null则使用原始文件名
	 * @param response         HTTP响应对象，不能为null
	 * @throws IOException              如果读取文件或写入响应过程中发生IO异常
	 * @throws IllegalArgumentException 如果file或response为null
	 * @since 1.0.0
	 */
	public static void writeFileToResponse(final File file, @Nullable final String downloadFilename,
										   final HttpServletResponse response) throws IOException {
		String contentType = FileUtils.getMimeType(file);
		long fileLength = file.length();
		setDownloadHeaders(fileLength, ObjectUtils.defaultIfNull(downloadFilename,
			file.getName()), contentType, response);
		try (InputStream inputStream = FileUtils.openUnsynchronizedBufferedInputStream(file)) {
			writeInputStreamToResponse(inputStream, response, contentType, HttpStatus.OK.value(), true);
		}
	}

	/**
	 * 将文件写入HTTP响应，可控制是否使用缓冲
	 * <p>
	 * 读取指定文件内容并写入HTTP响应流，自动设置适当的下载头信息。
	 * 通过buffer参数控制是否使用缓冲流，对大文件传输尤其有用。
	 * </p>
	 *
	 * @param file             要写入的文件，不能为null
	 * @param downloadFilename 下载时显示的文件名，如为null则使用原始文件名
	 * @param response         HTTP响应对象，不能为null
	 * @param buffer           是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @throws IOException              如果读取文件或写入响应过程中发生IO异常
	 * @throws IllegalArgumentException 如果file或response为null
	 * @since 1.0.0
	 */
	public static void writeFileToResponse(final File file, @Nullable final String downloadFilename,
										   final HttpServletResponse response, final boolean buffer) throws IOException {
		String contentType = FileUtils.getMimeType(file);
		long fileLength = file.length();
		setDownloadHeaders(fileLength, ObjectUtils.defaultIfNull(downloadFilename,
			file.getName()), contentType, response);
		try (InputStream inputStream = buffer ? FileUtils.openUnsynchronizedBufferedInputStream(file) : FileUtils.openInputStream(file)) {
			writeInputStreamToResponse(inputStream, response, contentType, HttpStatus.OK.value(), buffer);
		}
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组，可以为null（会被转换为空数组）
	 * @param response HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			HttpStatus.OK.value(), true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status.value(),
			true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, final int status) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status, true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @throws IllegalArgumentException 当response为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, @Nullable final String contentType) {
		writeBytesToResponse(bytes, response, contentType, HttpStatus.OK.value(), true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
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
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											@Nullable final String contentType, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, contentType, status.value(), true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和状态码
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 如果字节数组为null，则使用空数组代替。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
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
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											@Nullable final String contentType, final int status) {
		writeBytesToResponse(bytes, response, contentType, status, true);
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, final boolean buffer) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			HttpStatus.OK.value(), buffer);
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status.value(),
			buffer);
	}

	/**
	 * 将字节数组写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用{@code application/octet-stream}内容类型和指定HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes    要写入的字节数组
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response, final int status,
											final boolean buffer) {
		writeBytesToResponse(bytes, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status, buffer);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param buffer      是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											@Nullable final String contentType, final boolean buffer) {
		writeBytesToResponse(bytes, response, contentType, HttpStatus.OK.value(), buffer);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param status      HTTP状态码枚举，不能为null
	 * @param buffer      是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response、status为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											@Nullable final String contentType, final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(bytes, response, contentType, status.value(), buffer);
	}

	/**
	 * 将字节数组写入HTTP响应，使用指定内容类型、状态码和可控的缓冲选项
	 * <p>
	 * 使用指定内容类型和HTTP状态码将字节数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合较大数据传输，提高I/O性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 如果字节数组为null，则使用空数组代替，确保安全写入。
	 * </p>
	 *
	 * @param bytes       要写入的字节数组，可以为null（会被转换为空数组）
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型，不能为空
	 * @param status      HTTP状态码数值
	 * @param buffer      是否启用响应缓冲，true表示使用缓冲输出流，false表示直接写入
	 * @throws IllegalArgumentException 当response为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeBytesToResponse(final byte[] bytes, final HttpServletResponse response,
											@Nullable final String contentType, final int status, final boolean buffer) {
		Assert.notNull(response, "response 不可为null");

		if (buffer) {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream())) {
				InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(ArrayUtils.nullToEmpty(bytes));
				response.setStatus(status);
				response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
				inputStream.transferTo(outputStream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			try (OutputStream outputStream = response.getOutputStream()) {
				response.setStatus(status);
				response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
				outputStream.write(ArrayUtils.nullToEmpty(bytes));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response或inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			HttpStatus.OK.value(), true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response、inputStream或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			status.value(), true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码数值
	 * @throws IllegalArgumentException 当response、inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final int status) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status, true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
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
												  @Nullable final String contentType) {
		writeInputStreamToResponse(inputStream, response, contentType, HttpStatus.OK.value(), true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
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
												  @Nullable final String contentType, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, contentType, status.value(), true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和状态码
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * 输入流会完整传输到响应输出流中。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
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
												  @Nullable final String contentType, final int status) {
		writeInputStreamToResponse(inputStream, response, contentType, status, true);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和200状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response或inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final boolean buffer) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			HttpStatus.OK.value(), buffer);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态枚举
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码枚举，不能为null
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response、inputStream或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE,
			status.value(), buffer);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用默认二进制流类型和指定状态码
	 * <p>
	 * 使用"application/octet-stream"内容类型和指定HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码数值
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response、inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  final int status, final boolean buffer) {
		writeInputStreamToResponse(inputStream, response, MediaType.APPLICATION_OCTET_STREAM_VALUE, status, buffer);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和默认200状态码
	 * <p>
	 * 使用指定内容类型和HTTP 200状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response、inputStream或contentType为null/空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  @Nullable final String contentType, final boolean buffer) {
		writeInputStreamToResponse(inputStream, response, contentType, HttpStatus.OK.value(), buffer);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型和状态枚举
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型
	 * @param status      HTTP状态码枚举，不能为null
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response、inputStream、status或contentType为null/空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  @Nullable final String contentType, final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");

		writeInputStreamToResponse(inputStream, response, contentType, status.value(), buffer);
	}

	/**
	 * 将输入流内容写入HTTP响应，使用指定内容类型、状态码和可控的缓冲选项
	 * <p>
	 * 使用指定内容类型和HTTP状态码将输入流数据写入响应。
	 * </p>
	 * <p>
	 * 可以通过buffer参数控制是否启用响应缓冲：
	 * <ul>
	 *   <li>当buffer为true时，系统会根据输入流类型选择最佳处理方式：
	 *     <ul>
	 *       <li>已缓冲的输入流(如BufferedInputStream等)将直接传输到缓冲输出流</li>
	 *       <li>未缓冲的输入流会被包装为缓冲输入流后传输，以提高性能</li>
	 *     </ul>
	 *   </li>
	 *   <li>当buffer为false时，直接将输入流内容传输到响应输出流，适用于小数据或需要立即刷新的场景</li>
	 * </ul>
	 * 输入流会完整传输到响应输出流中，并自动应用适当的HTTP头信息。
	 * </p>
	 *
	 * @param inputStream 要写入的输入流，不能为null
	 * @param response    HTTP响应对象，不能为null
	 * @param contentType 响应内容类型，不能为空
	 * @param status      HTTP状态码数值
	 * @param buffer      是否启用响应缓冲，true表示根据输入流类型选择最佳缓冲方式，false表示直接写入
	 * @throws IllegalArgumentException 当response、inputStream为null或contentType为空时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static void writeInputStreamToResponse(final InputStream inputStream, final HttpServletResponse response,
												  @Nullable final String contentType, final int status, final boolean buffer) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(inputStream, "inputStream 不可为null");

		if (buffer) {
			if (inputStream instanceof BufferedInputStream ||
				inputStream instanceof UnsynchronizedByteArrayInputStream ||
				inputStream instanceof UnsynchronizedBufferedInputStream ||
				inputStream instanceof BufferedFileChannelInputStream) {
				try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream())) {
					response.setStatus(status);
					response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
					inputStream.transferTo(outputStream);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			} else {
				try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream());
					 InputStream bufferedInputStream = IOUtils.unsynchronizedBuffer(inputStream)) {
					response.setStatus(status);
					response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
					bufferedInputStream.transferTo(outputStream);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		} else {
			try (OutputStream outputStream = response.getOutputStream()) {
				response.setStatus(status);
				response.setContentType(ObjectUtils.defaultIfNull(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE));
				inputStream.transferTo(outputStream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * 将HTTP异常信息写入响应
	 * <p>
	 * 根据异常类型上的@HttpException注解配置，将异常信息转换为标准Result格式并写入响应。
	 * 如异常类型上没有注解，则使用默认错误码和HTTP 200状态码。
	 * 根据异常注解配置决定是否记录错误日志。
	 * 默认启用响应缓冲以提高传输性能。
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
		writeHttpExceptionToResponse(httpException, response, true);
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
	 * <p>
	 * 通过buffer参数可控制响应输出方式：
	 * <ul>
	 *   <li>当buffer为true时，使用缓冲输出流，适合大多数场景，可提高传输性能</li>
	 *   <li>当buffer为false时，直接写入响应流，适合小数据量或需要立即发送的场景</li>
	 * </ul>
	 * </p>
	 *
	 * @param httpException HTTP异常对象，不能为null
	 * @param response      HTTP响应对象，不能为null
	 * @param buffer        是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <E>           继承自BaseHttpException的异常类型
	 * @throws IllegalArgumentException 当response或httpException为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <E extends BaseHttpException> void writeHttpExceptionToResponse(final E httpException,
																				  final HttpServletResponse response,
																				  boolean buffer) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(httpException, "httpException 不可为null");

		HttpException annotation = httpException.getClass().getAnnotation(HttpException.class);
		if (Objects.nonNull(annotation)) {
			Result<Void> result = Result.fail(annotation.type().computeCode(annotation.code()), httpException.getMessage());
			if (annotation.log()) {
				httpException.log(LOGGER, Level.ERROR);
			}
			writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
				MediaType.APPLICATION_JSON_VALUE, annotation.status().value(), buffer);
		} else {
			Result<Void> result = Result.fail(WebConstants.BASE_ERROR_CODE, httpException.getMessage());
			httpException.log(LOGGER, Level.ERROR);
			writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
				MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), buffer);
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
	 * <p>
	 *     默认启用响应缓冲以提高传输性能。
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
		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), true);
	}

	/**
	 * 将JavaBean对象以JSON格式写入响应，使用指定状态枚举
	 * <p>
	 * 将对象包装为{@link Result#ok(T)}并以JSON格式写入响应，使用指定HTTP状态码。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 * <p>
	 *     默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param bean     要写入的JavaBean对象
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, status, true);
	}

	/**
	 * 将JavaBean对象以JSON格式写入响应，使用指定状态码
	 * <p>
	 * 将对象包装为{@link Result#ok(T)}并以JSON格式写入响应，使用指定HTTP状态码。
	 * </p>
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 * <p>
	 *     默认启用响应缓冲以提高传输性能。
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
		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, status, true);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用HTTP 200状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 * <p>
	 *     默认启用响应缓冲以提高传输性能。
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
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), true);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态枚举
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 * <p>
	 *     默认启用响应缓冲以提高传输性能。
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
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response, final HttpStatus status) {
		Assert.notNull(status, "status 不可为null");
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, status.value(), true);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 * <p>
	 * 默认启用响应缓冲以提高传输性能。
	 * </p>
	 *
	 * @param result   要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response, final int status) {
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, status, true);
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
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final boolean buffer) {
		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), buffer);
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
	 * @param bean     要写入的JavaBean对象
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response,
											   final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");

		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, status, buffer);
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
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Bean对象类型
	 * @throws IllegalArgumentException 当response为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeBeanToResponse(final T bean, final HttpServletResponse response, final int status,
											   final boolean buffer) {
		writeBytesToResponse(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8),
			response, MediaType.APPLICATION_JSON_VALUE, status, buffer);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用HTTP 200状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result   要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response,
												 final boolean buffer) {
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), buffer);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态枚举
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result   要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码枚举，不能为null
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response、result或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response,
												 final HttpStatus status, final boolean buffer) {
		Assert.notNull(status, "status 不可为null");
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, status.value(), buffer);
	}

	/**
	 * 将{@link Result}以JSON格式写入响应，使用指定状态码
	 * <p>
	 * 响应使用UTF-8字符集编码，内容类型为{@code application/json}。
	 * </p>
	 *
	 * @param result   要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @param buffer   是否启用响应缓冲，true表示使用缓冲流，false表示直接写入
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response,
												 final int status, final boolean buffer) {
		Assert.notNull(result, "result 不可为null");

		writeBytesToResponse(result.toString().getBytes(StandardCharsets.UTF_8), response,
			MediaType.APPLICATION_JSON_VALUE, status, buffer);
	}
}