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

import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.model.common.Result;
import io.github.pangju666.framework.web.pool.WebConstants;
import jakarta.servlet.http.HttpServletResponse;
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
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 1. 设置文件下载响应头
 * ResponseUtils.setAttachmentHeader(response, "report.pdf");
 *
 * // 2. 将字节数组写入响应
 * byte[] data = generatePdfData();
 * ResponseUtils.writeBytesToResponse(data, response, "application/pdf");
 *
 * // 3. 将Java对象转为JSON响应
 * User user = userService.getCurrentUser();
 * ResponseUtils.writeBeanToResponse(user, response);
 *
 * // 4. 处理异常并输出到响应
 * try {
 *     // 业务逻辑
 * } catch (ResourceNotFoundException e) {
 *     ResponseUtils.writeHttpExceptionToResponse(e, response);
 * }
 *
 * // 5. 从输入流写入响应
 * try (InputStream fileStream = new FileInputStream(file)) {
 *     ResponseUtils.writeInputStreamToResponse(fileStream, response, "image/jpeg");
 * }
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

		try (OutputStream outputStream = response.getOutputStream();
			 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
			InputStream inputStream = IOUtils.toUnsynchronizedByteArrayInputStream(ArrayUtils.nullToEmpty(bytes));
			response.setStatus(status);
			response.setContentType(contentType);
			inputStream.transferTo(bufferedOutputStream);
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
	 * @param response    HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 当response或inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
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
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码枚举，不能为null
	 * @throws IllegalArgumentException 当response、inputStream或status为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
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
	 * @param response    HTTP响应对象，不能为null
	 * @param status      HTTP状态码数值
	 * @throws IllegalArgumentException 当response、inputStream为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
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

		try (BufferedInputStream bufferedInputStream = IOUtils.buffer(inputStream);
			 OutputStream outputStream = response.getOutputStream();
			 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
			response.setStatus(status);
			response.setContentType(contentType);
			bufferedInputStream.transferTo(bufferedOutputStream);
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
	 * @param result   要写入的Result对象，不能为null
	 * @param response HTTP响应对象，不能为null
	 * @param status   HTTP状态码数值
	 * @param <T>      Result中的数据类型
	 * @throws IllegalArgumentException 当response或result为null时抛出
	 * @throws UncheckedIOException     写入过程发生IO异常时抛出
	 * @since 1.0.0
	 */
	public static <T> void writeResultToResponse(final Result<T> result, final HttpServletResponse response, final int status) {
		Assert.notNull(response, "response 不可为null");
		Assert.notNull(result, "result 不可为null");

		try (PrintWriter writer = response.getWriter()) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			response.setStatus(status);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			writer.write(result.toString());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}