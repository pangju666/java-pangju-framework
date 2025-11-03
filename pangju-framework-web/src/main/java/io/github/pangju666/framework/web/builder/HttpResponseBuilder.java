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

package io.github.pangju666.framework.web.builder;

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
 * Http响应构建器
 * <p>
 * 提供了一套便捷的方法用于简化HttpServletResponse的操作，支持链式调用。
 * 主要功能包括：
 * <ul>
 *   <li>设置响应状态码和内容类型</li>
 *   <li>配置文件下载响应头</li>
 *   <li>写入各种类型的响应数据（流、字节数组、文件、JSON等）</li>
 *   <li>处理HTTP异常并生成统一的错误响应</li>
 *   <li>支持缓冲模式优化I/O性能</li>
 * </ul>
 * </p>
 * <p>
 * 默认启用缓冲模式以提高性能。
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
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
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
	public static HttpResponseBuilder fromResponse(final HttpServletResponse response) {
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
	 * 设置是否启用缓冲模式
	 * <p>
	 * 支持链式调用。缓冲模式可以提高I/O性能，但会占用额外的内存。
	 * 在处理大文件时，可以考虑关闭缓冲模式以减少内存占用。
	 * </p>
	 *
	 * @param buffer 是否启用缓冲，true为启用，false为禁用
	 * @return 当前HttpResponseBuilder实例，支持链式调用
	 * @since 1.0.0
	 */
	public HttpResponseBuilder buffer(final boolean buffer) {
		this.buffer = buffer;

		return this;
	}

	/**
	 * 设置文件下载的Content-Disposition响应头
	 * <p>
	 * 支持链式调用。使用UTF-8字符集对文件名进行URL编码。
	 * 如果filename为null或空白字符串，则不执行任何操作。
	 * </p>
	 *
	 * @param filename 下载文件名，可以为null
	 * @since 1.0.0
	 */
	public HttpResponseBuilder setAttachmentHeader(@Nullable final String filename) {
		setAttachmentHeader(filename, StandardCharsets.UTF_8);

		return this;
	}

	/**
	 * 设置文件下载的Content-Disposition响应头
	 * <p>
	 * 支持链式调用。使用指定的字符集对文件名进行URL编码。
	 * 如果filename为null或空白字符串，则不执行任何操作。
	 * 如果charsets为null，则使用UTF-8作为默认字符集。
	 * </p>
	 *
	 * @param filename 下载文件名，可以为null
	 * @param charsets 字符集，可以为null（默认使用UTF-8）
	 * @since 1.0.0
	 */
	public HttpResponseBuilder setAttachmentHeader(@Nullable final String filename, @Nullable final Charset charsets) {
		if (StringUtils.isNotBlank(filename)) {
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
				URLEncoder.encode(filename, ObjectUtils.defaultIfNull(charsets, StandardCharsets.UTF_8)));
		}

		return this;
	}

	/**
	 * 将输入流的内容写入响应输出流
	 * <p>
	 * 根据buffer标志决定是否使用缓冲模式：
	 * <ul>
	 *   <li>启用缓冲时：如果输入流已经是缓冲流，则直接使用；否则包装为缓冲流</li>
	 *   <li>禁用缓冲时：直接将输入流内容传输到输出流</li>
	 * </ul>
	 * 注意：此方法不会关闭传入的inputStream，需要调用者自行关闭。
	 * </p>
	 *
	 * @param inputStream 输入流，不能为null
	 * @throws IllegalArgumentException 如果inputStream为null
	 * @throws UncheckedIOException     如果发生I/O错误
	 * @since 1.0.0
	 */
	public void write(final InputStream inputStream) {
		Assert.notNull(inputStream, "inputStream 不可为null");

		if (buffer) {
			try (OutputStream outputStream = IOUtils.buffer(response.getOutputStream())) {
				if (inputStream instanceof BufferedInputStream ||
					inputStream instanceof UnsynchronizedByteArrayInputStream ||
					inputStream instanceof UnsynchronizedBufferedInputStream ||
					inputStream instanceof BufferedFileChannelInputStream) {
					inputStream.transferTo(outputStream);
				} else {
					try (InputStream bufferedInputStream = IOUtils.unsynchronizedBuffer(inputStream)) {
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
	 * 将字节数组写入响应输出流
	 * <p>
	 * 自动设置Content-Length响应头。
	 * 根据buffer标志和字节数组是否为空选择不同的写入策略：
	 * <ul>
	 *   <li>字节数组为空：直接写入空字节数组</li>
	 *   <li>启用缓冲且字节数组不为空：使用缓冲输出流写入</li>
	 *   <li>禁用缓冲且字节数组不为空：直接写入输出流</li>
	 * </ul>
	 * </p>
	 *
	 * @param bytes 字节数组，可以为null或空数组
	 * @throws UncheckedIOException 如果发生I/O错误
	 * @since 1.0.0
	 */
	public void write(final byte[] bytes) {
		response.setContentLength(ArrayUtils.getLength(bytes));
		if (ArrayUtils.isEmpty(bytes)) {
			try (OutputStream outputStream = response.getOutputStream()) {
				outputStream.write(ArrayUtils.EMPTY_BYTE_ARRAY);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			try (OutputStream outputStream = buffer ? IOUtils.buffer(response.getOutputStream()) : response.getOutputStream()) {
				outputStream.write(bytes);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	/**
	 * 将文件内容写入响应输出流
	 * <p>
	 * 自动执行以下操作：
	 * <ul>
	 *   <li>根据文件扩展名检测并设置MIME类型</li>
	 *   <li>设置文件下载响应头（使用原文件名）</li>
	 *   <li>根据buffer标志选择是否使用缓冲流读取文件</li>
	 * </ul>
	 * </p>
	 *
	 * @param file 要写入的文件对象，不能为null
	 * @throws IOException 如果文件读取或写入失败
	 * @since 1.0.0
	 */
	public void writeFile(final File file) throws IOException {
		response.setContentType(FileUtils.getMimeType(file));
		setAttachmentHeader(file.getName());
		try (InputStream inputStream = buffer ? FileUtils.openUnsynchronizedBufferedInputStream(file) : FileUtils.openInputStream(file)) {
			write(inputStream);
		}
	}

	/**
	 * 将文件内容写入响应输出流，并指定下载文件名
	 * <p>
	 * 自动执行以下操作：
	 * <ul>
	 *   <li>根据文件扩展名检测并设置MIME类型</li>
	 *   <li>设置文件下载响应头（使用指定的下载文件名，如果为空则使用原文件名）</li>
	 *   <li>根据buffer标志选择是否使用缓冲流读取文件</li>
	 * </ul>
	 * </p>
	 *
	 * @param file             要写入的文件对象，不能为null
	 * @param downloadFilename 下载时显示的文件名，可以为null（使用原文件名）
	 * @throws IOException 如果文件读取或写入失败
	 * @since 1.0.0
	 */
	public void writeFile(final File file, @Nullable final String downloadFilename) throws IOException {
		response.setContentType(FileUtils.getMimeType(file));
		response.setContentLength((int) file.length());
		setAttachmentHeader(StringUtils.defaultIfBlank(downloadFilename, file.getName()));
		try (InputStream inputStream = buffer ? FileUtils.openUnsynchronizedBufferedInputStream(file) : FileUtils.openInputStream(file)) {
			write(inputStream);
		}
	}

	/**
	 * 将Java对象以JSON格式写入响应输出流
	 * <p>
	 * 该方法会智能处理传入的对象类型：
	 * <ul>
	 *   <li>如果对象已经是{@link Result}类型：直接序列化为JSON并写入响应</li>
	 *   <li>如果对象是其他类型：自动包装为{@code Result.ok(bean)}后再序列化写入</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 自动执行以下操作：
	 * <ul>
	 *   <li>设置Content-Type为application/json</li>
	 *   <li>使用UTF-8字符集编码</li>
	 *   <li>将对象转换为JSON字符串并写入响应输出流</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 使用示例：
	 * <pre>{@code
	 * // 示例1：写入普通对象，自动包装为Result.ok()
	 * User user = new User("张三", 25);
	 * responseHelper.writeBean(user);
	 * // 响应: {"code": 200, "message": "success", "data": {"name": "张三", "age": 25}}
	 *
	 * // 示例2：写入Result对象，直接序列化
	 * Result<User> result = Result.ok(user);
	 * responseHelper.writeBean(result);
	 * // 响应: {"code": 200, "message": "success", "data": {"name": "张三", "age": 25}}
	 * }</pre>
	 * </p>
	 *
	 * <p>
	 * 注意事项：
	 * <ul>
	 *   <li>bean参数可以为null，会被包装为Result.ok()</li>
	 *   <li>对象序列化依赖于Result类的toString()方法实现</li>
	 * </ul>
	 * </p>
	 *
	 * @param <T>  对象类型
	 * @param bean 要写入的Java对象，可以是任意类型包括Result
	 * @throws UncheckedIOException 如果写入响应流时发生I/O错误
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
	 * 处理HTTP异常并将错误信息以JSON格式写入响应输出流
	 * <p>
	 * 处理流程：
	 * <ol>
	 *   <li>检查异常类是否有@HttpException注解</li>
	 *   <li>如果有注解：
	 *     <ul>
	 *       <li>根据注解配置的错误码类型和错误码生成失败Result</li>
	 *       <li>如果注解配置了需要记录日志，则按指定级别记录日志</li>
	 *       <li>设置响应状态码为注解中配置的状态码</li>
	 *     </ul>
	 *   </li>
	 *   <li>如果没有注解：
	 *     <ul>
	 *       <li>使用默认错误码生成失败Result</li>
	 *       <li>记录ERROR级别日志</li>
	 *     </ul>
	 *   </li>
	 *   <li>设置Content-Type为application/json</li>
	 *   <li>将错误Result序列化为JSON并写入响应</li>
	 * </ol>
	 * </p>
	 *
	 * @param <E>           HTTP异常类型，必须继承自BaseHttpException
	 * @param httpException HTTP异常对象，不能为null
	 * @since 1.0.0
	 */
	public <E extends BaseHttpException> void writeHttpException(final E httpException) {
		HttpException annotation = httpException.getClass().getAnnotation(HttpException.class);
		Result<Void> result;

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