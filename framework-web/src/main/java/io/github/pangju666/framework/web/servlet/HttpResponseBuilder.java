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

package io.github.pangju666.framework.web.servlet;

import io.github.pangju666.commons.io.utils.FileUtils;
import io.github.pangju666.commons.io.utils.IOUtils;
import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import io.github.pangju666.framework.web.lang.WebConstants;
import io.github.pangju666.framework.web.model.Result;
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
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Http响应构建器
 * <p>
 * 对 {@link jakarta.servlet.http.HttpServletResponse} 的常用写出操作进行封装，提供更简洁一致的使用方式，支持链式调用。
 * </p>
 * <p>
 * 功能概览：
 * <ul>
 *   <li>设置响应状态码与内容类型</li>
 *   <li>配置下载相关的响应头（如 Content-Disposition）</li>
 *   <li>写入多种数据源：输入流、字节数组、文件、JSON（统一封装为 {@link io.github.pangju666.framework.web.model.Result}）</li>
 *   <li>处理带有 {@link io.github.pangju666.framework.web.annotation.HttpException} 的异常为标准错误响应</li>
 *   <li>可选缓冲模式以优化 I/O 性能</li>
 * </ul>
 * </p>
 * <p>
 * 默认行为：
 * <ul>
 *   <li>在实际写出前如未设置字符集，则默认使用 {@code UTF-8}</li>
 *   <li>JSON 写出统一设置 {@code Content-Type: application/json}</li>
 *   <li>写入方法不会主动关闭调用方提供的输入流，需要调用方自行管理</li>
 * </ul>
 * </p>
 * <p>
 * 使用建议：
 * <ul>
 *   <li>写二进制数据前如需明确类型，请先调用 {@link #contentType(String)}</li>
 *   <li>下载文件建议显式设置文件名并确保兼容性字符集（默认 UTF-8）</li>
 *   <li>缓存控制可优先使用 {@link org.springframework.http.CacheControl} 构建器以生成标准化头值</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
public class HttpResponseBuilder {
	/**
	 * 类日志记录器实例
	 * <p>
	 * 用于记录工具类内部操作日志，主要记录异常和错误信息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseBuilder.class);
	/**
	 * HTTP响应对象
	 * <p>
	 * 封装的HttpServletResponse实例，所有响应操作都基于此对象。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final HttpServletResponse response;
	/**
	 * 是否启用缓冲模式
	 * <p>
	 * 当为true时，输出流会使用缓冲区以提高I/O性能。
	 * 默认值为true。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private boolean buffer = true;
	/**
	 * 缓冲区大小
	 * <p>
	 * 默认值为{@link IOUtils#DEFAULT_BUFFER_SIZE}。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private int bufferSize = IOUtils.DEFAULT_BUFFER_SIZE;

	/**
	 * 构造HTTP响应辅助类实例
	 * <p>
	 * 初始化响应对象并设置默认配置：
	 * <ul>
	 *   <li>状态码设置为200 OK</li>
	 *   <li>内容类型设置为application/octet-stream</li>
	 * </ul>
	 * </p>
	 *
	 * @param response HTTP响应对象，不能为null
	 * @throws IllegalArgumentException 如果response参数为null
	 * @since 1.0.0
	 */
	protected HttpResponseBuilder(final HttpServletResponse response) {
		Assert.notNull(response, "response 不可为null");

		this.response = response;
	}

	/**
	 * 从HttpServletResponse对象创建HttpResponseBuilder实例的工厂方法
	 * <p>
	 * 这是一个静态工厂方法,提供了更语义化的方式来创建HttpResponseBuilder实例。
	 * 相比直接使用构造函数,使用此方法可以使代码更加清晰易读。
	 * </p>
	 * <p>
	 * 创建的实例会自动配置以下默认值:
	 * <ul>
	 *   <li>HTTP状态码: 200 OK</li>
	 *   <li>Content-Type: application/octet-stream</li>
	 *   <li>缓冲模式: 启用</li>
	 * </ul>
	 * </p>
	 *
	 * @param response HTTP响应对象,不能为null
	 * @return HttpResponseBuilder实例
	 * @throws IllegalArgumentException 如果response参数为null
	 * @see #HttpResponseBuilder(HttpServletResponse)
	 * @since 1.0.0
	 */
	public static HttpResponseBuilder from(final HttpServletResponse response) {
		return new HttpResponseBuilder(response);
	}

	/**
	 * 设置响应内容类型
	 * <p>
	 * 支持链式调用。如果传入的contentType为null或空白字符串，则不执行任何操作。
	 * </p>
	 *
	 * @param contentType 内容类型（MIME类型），如"application/json"、"text/html"等
	 * @return 当前HttpResponseBuilder实例，支持链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder contentType(final String contentType) {
		if (StringUtils.isNotBlank(contentType)) {
			response.setContentType(contentType);
		}

		return this;
	}

	/**
	 * 设置响应状态码
	 * <p>
	 * 支持链式调用。使用Spring的HttpStatus枚举类型设置状态码。
	 * 如果传入的status为null，则不执行任何操作。
	 * </p>
	 *
	 * @param status HTTP状态码枚举对象
	 * @return 当前HttpResponseBuilder实例，支持链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder status(final HttpStatus status) {
		if (Objects.nonNull(status)) {
			response.setStatus(status.value());
		}

		return this;
	}

	/**
	 * 设置响应状态码
	 * <p>
	 * 支持链式调用。使用整数值直接设置状态码。
	 * 状态码必须大于等于100，否则不执行任何操作。
	 * </p>
	 *
	 * @param status HTTP状态码整数值，必须>=100
	 * @return 当前HttpResponseBuilder实例，支持链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder status(final int status) {
		if (status >= 100) {
			response.setStatus(status);
		}

		return this;
	}

	/**
	 * 启用缓冲模式
	 * <p>
	 * 开启后写出操作将使用缓冲输出，提高 I/O 性能。缓冲大小使用默认值。
	 * </p>
	 *
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder buffer() {
		this.buffer = true;

		return this;
	}

	/**
	 * 启用缓冲模式并设置缓冲区大小
	 * <p>
	 * 适用于大文件或高并发场景下的 I/O 优化。
	 * </p>
	 *
	 * @param bufferSize 缓冲区大小（字节），应为正值
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder buffer(final int bufferSize) {
		this.buffer = true;
		this.bufferSize = bufferSize;

		return this;
	}

	/**
	 * 设置响应字符编码（字符串形式）
	 * <p>
	 * 如果传入为空白则不生效。建议与 {@link #contentType(String)} 搭配设置明确的响应类型与编码。
	 * </p>
	 *
	 * @param charset 字符集名称，例如 "UTF-8"
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder characterEncoding(final String charset) {
		if (StringUtils.isNotBlank(charset)) {
			this.response.setCharacterEncoding(charset);
		}

		return this;
	}

	/**
	 * 设置响应字符编码（{@link java.nio.charset.Charset}）
	 * <p>
	 * 如果传入为 null 则不生效。
	 * </p>
	 *
	 * @param charset 字符集对象，例如 {@link java.nio.charset.StandardCharsets#UTF_8}
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder characterEncoding(final Charset charset) {
		if (Objects.nonNull(charset)) {
			response.setCharacterEncoding(charset.name());
		}

		return this;
	}

	/**
	 * 设置简易缓存控制头（秒级）
	 * <p>
	 * 将 {@code Cache-Control} 头值设置为传入时长的秒数字符串。若需标准化头值（如 {@code max-age=...}、{@code no-cache}），建议使用
	 * {@link #cacheControl(CacheControl)}。
	 * </p>
	 *
	 * @param maxAge 最大缓存时长
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder cacheControl(final Duration maxAge) {
		if (Objects.nonNull(maxAge)) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, String.valueOf(maxAge.toSeconds()));
		}

		return this;
	}

	/**
	 * 使用 {@link org.springframework.http.CacheControl} 设置标准缓存控制头
	 * <p>
	 * 通过 Spring 提供的构建器生成规范的 {@code Cache-Control} 头值，推荐在需要明确缓存策略时使用。
	 * </p>
	 *
	 * @param cacheControl 缓存控制配置
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder cacheControl(final CacheControl cacheControl) {
		if (Objects.nonNull(cacheControl)) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl.getHeaderValue());
		}

		return this;
	}

	/**
	 * 设置文件下载的 Content-Disposition 响应头
	 * <p>
	 * 采用简单的 {@code attachment;filename=<encoded>} 形式并对文件名进行 URL 编码（默认 UTF-8）。
	 * 若文件名为空白则不设置该头。兼容性较好，若需 RFC 6266 完整支持（如 {@code filename*}），可在上层自行扩展。
	 * </p>
	 *
	 * @param filename 下载文件名，可以为 null
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder contentDisposition(@Nullable final String filename) {
		contentDisposition(filename, StandardCharsets.UTF_8);

		return this;
	}

	/**
	 * 设置文件下载的 Content-Disposition 响应头（自定义字符集）
	 * <p>
	 * 使用指定字符集对文件名进行 URL 编码；若字符集为 null，默认使用 UTF-8。文件名为空白时不设置该头。
	 * </p>
	 *
	 * @param filename 下载文件名，可以为 null
	 * @param charsets 字符集，可为 null（默认 UTF-8）
	 * @return 当前实例，用于链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder contentDisposition(@Nullable final String filename, @Nullable final Charset charsets) {
		if (StringUtils.isNotBlank(filename)) {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
				URLEncoder.encode(filename, ObjectUtils.getIfNull(charsets, StandardCharsets.UTF_8)));
		}

		return this;
	}

	/**
	 * 将输入流内容写入响应输出
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>如未设置 {@code Content-Type}，默认使用 {@code application/octet-stream}</li>
	 *   <li>如未设置字符编码，默认使用 {@code UTF-8}</li>
	 *   <li>开启缓冲时，尽可能复用或包装为缓冲输入流，并使用缓冲输出提高性能</li>
	 *   <li>禁用缓冲时，直接传输至响应输出流</li>
	 * </ul>
	 * 注意：该方法不会关闭调用方提供的 {@code inputStream}，请自行管理其生命周期。
	 * </p>
	 *
	 * @param inputStream 输入流，不能为 null
	 * @throws IllegalArgumentException 当 {@code inputStream} 为 null 时抛出
	 * @throws UncheckedIOException     写出发生 I/O 错误时抛出
	 * @since 1.0.0
	 */
	public void write(final InputStream inputStream) {
		Assert.notNull(inputStream, "inputStream 不可为null");

		if (StringUtils.isBlank(response.getContentType())) {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		}
		if (StringUtils.isBlank(response.getCharacterEncoding())) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		}
		if (buffer) {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream(), bufferSize)) {
				if (inputStream instanceof BufferedInputStream ||
					inputStream instanceof UnsynchronizedByteArrayInputStream ||
					inputStream instanceof UnsynchronizedBufferedInputStream ||
					inputStream instanceof BufferedFileChannelInputStream) {
					inputStream.transferTo(outputStream);
				} else {
					try (InputStream bufferedInputStream = IOUtils.unsynchronizedBuffer(inputStream, bufferSize)) {
						bufferedInputStream.transferTo(outputStream);
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			try (OutputStream outputStream = response.getOutputStream()) {
				inputStream.transferTo(outputStream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * 将字节数组写入响应输出
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>自动设置 {@code Content-Length}</li>
	 *   <li>如未设置 {@code Content-Type}，默认使用 {@code application/octet-stream}</li>
	 *   <li>如未设置字符编码，默认使用 {@code UTF-8}</li>
	 *   <li>开启缓冲时使用缓冲输出；禁用缓冲时直接写出</li>
	 * </ul>
	 * </p>
	 *
	 * @param bytes 字节数组，可为 null 或空数组
	 * @throws UncheckedIOException 写出发生 I/O 错误时抛出
	 * @since 1.0.0
	 */
	public void write(final byte[] bytes) {
		if (StringUtils.isBlank(response.getContentType())) {
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		}
		if (StringUtils.isBlank(response.getCharacterEncoding())) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		}
		response.setContentLength(ArrayUtils.getLength(bytes));
		if (ArrayUtils.isNotEmpty(bytes)) {
			try (OutputStream outputStream = buffer ? IOUtils.buffer(response.getOutputStream(), bufferSize) : response.getOutputStream()) {
				outputStream.write(bytes);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * 将文件内容写入响应
	 * <p>
	 * 自动操作：
	 * <ul>
	 *   <li>根据扩展名检测并设置 MIME 类型</li>
	 *   <li>设置下载响应头（使用原文件名）</li>
	 *   <li>设置 {@code Content-Length}</li>
	 *   <li>按缓冲设置选择读取方式</li>
	 * </ul>
	 * </p>
	 *
	 * @param file 要写入的文件对象，不能为 null
	 * @throws IOException 文件读取或写入失败时抛出
	 * @since 1.0.0
	 */
	public void writeFile(final File file) throws IOException {
		response.setContentType(FileUtils.getMimeType(file));
		response.setContentLength((int) file.length());
		contentDisposition(file.getName());
		try (InputStream inputStream = buffer ? FileUtils.openUnsynchronizedBufferedInputStream(file) : FileUtils.openInputStream(file)) {
			write(inputStream);
		}
	}

	/**
	 * 将文件内容写入响应（指定下载文件名）
	 * <p>
	 * 自动操作：
	 * <ul>
	 *   <li>根据扩展名检测并设置 MIME 类型</li>
	 *   <li>设置下载响应头（使用指定文件名，空白则使用原文件名）</li>
	 *   <li>设置 {@code Content-Length}</li>
	 *   <li>按缓冲设置选择读取方式</li>
	 * </ul>
	 * </p>
	 *
	 * @param file             要写入的文件对象，不能为 null
	 * @param downloadFilename 下载文件名，可为 null（使用原文件名）
	 * @throws IOException 文件读取或写入失败时抛出
	 * @since 1.0.0
	 */
	public void writeFile(final File file, @Nullable final String downloadFilename) throws IOException {
		response.setContentType(FileUtils.getMimeType(file));
		response.setContentLength((int) file.length());
		contentDisposition(StringUtils.defaultIfBlank(downloadFilename, file.getName()));
		try (InputStream inputStream = buffer ? FileUtils.openUnsynchronizedBufferedInputStream(file) : FileUtils.openInputStream(file)) {
			write(inputStream);
		}
	}

	/**
	 * 将 Java 对象以 JSON 写入响应
	 * <p>
	 * 行为说明：
	 * <ul>
	 *   <li>入参为 {@link Result} 时直接序列化；其他对象会包装为 {@code Result.ok(bean)}</li>
	 *   <li>设置 {@code Content-Type: application/json}，编码使用 {@code UTF-8}</li>
	 *   <li>序列化依赖 {@link Result#toString()} 的实现</li>
	 * </ul>
	 * </p>
	 *
	 * @param <T>  对象类型
	 * @param bean Java 对象，可为 {@code null}
	 * @throws UncheckedIOException 写出发生 I/O 错误时抛出
	 * @see Result
	 * @since 1.0.0
	 */
	public <T> void writeBean(@Nullable final T bean) {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		if (bean instanceof Result<?> result) {
			write(result.toString().getBytes(StandardCharsets.UTF_8));
		} else {
			write(Result.ok(bean).toString().getBytes(StandardCharsets.UTF_8));
		}
	}

	/**
	 * 写出 HTTP 异常为统一错误响应（JSON）
	 * <p>
	 * 处理逻辑：
	 * <ol>
	 *   <li>检测异常类上的 {@link io.github.pangju666.framework.web.annotation.HttpException} 注解</li>
	 *   <li>存在注解时：根据注解计算业务错误码，按配置记录日志并设置 HTTP 状态码</li>
	 *   <li>无注解时：使用通用基础错误码，记录 ERROR 级别日志</li>
	 *   <li>统一设置 {@code Content-Type: application/json} 并写出序列化后的 {@link Result}</li>
	 * </ol>
	 * 该方法会禁用缓冲模式以尽快写出错误响应。
	 * </p>
	 *
	 * @param <E>           HTTP 异常类型，必须继承 {@link io.github.pangju666.framework.web.exception.base.BaseHttpException}
	 * @param httpException HTTP 异常对象，不能为 null
	 * @since 1.0.0
	 */
	public <E extends BaseHttpException> void writeHttpException(final E httpException) {
        this.buffer = false;
        Result<Void> result;

		HttpException annotation = httpException.getClass().getAnnotation(HttpException.class);
		if (Objects.nonNull(annotation)) {
			result = Result.fail(annotation.type().computeCode(annotation.code()), httpException.getMessage());
			if (annotation.log()) {
				httpException.log(LOGGER, annotation.level());
			}
			this.response.setStatus(annotation.status().value());
		} else {
			result = Result.fail(WebConstants.BASE_ERROR_CODE, httpException.getMessage());
			httpException.log(LOGGER, Level.ERROR);
		}

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		write(result.toString().getBytes());
	}
}