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

package io.github.pangju666.framework.web.exception.remote;

import io.github.pangju666.framework.web.exception.base.ServiceException;
import io.github.pangju666.framework.web.lang.pool.WebConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * 远程服务调用异常
 * <p>
 * 用于封装远程服务调用过程中发生的异常情况。此异常包含详细的远程服务错误信息，
 * 便于进行错误追踪和日志记录。
 * </p>
 *
 * <p>
 * 特性：
 * <ul>
 *     <li>包含完整的远程服务错误信息</li>
 *     <li>支持自定义错误消息</li>
 *     <li>支持异常链传递</li>
 *     <li>提供结构化的日志输出</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see RemoteServiceError
 * @see ServiceException
 * @since 1.0.0
 */
public class RemoteServiceException extends ServiceException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未指定自定义错误消息时使用的默认消息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String DEFAULT_MESSAGE = "远程服务请求失败";

	/**
	 * 远程服务错误信息
	 * <p>
	 * 包含远程服务调用的详细错误信息，如服务名称、接口名称、URI等。
	 * 此字段为final，确保错误信息在异常实例创建后不可更改。
	 * </p>
	 *
	 * @see RemoteServiceError
	 * @since 1.0.0
	 */
	protected final RemoteServiceError error;

	/**
	 * 使用默认错误消息创建异常实例
	 *
	 * @param error 远程服务错误信息
	 * @since 1.0.0
	 */
	public RemoteServiceException(RemoteServiceError error) {
		super(WebConstants.REMOTE_SERVICE_ERROR_CODE, DEFAULT_MESSAGE);
		this.error = error;
	}

	/**
	 * 使用自定义错误消息创建异常实例
	 *
	 * @param error 远程服务错误信息
	 * @param message 自定义错误消息
	 * @since 1.0.0
	 */
	public RemoteServiceException(RemoteServiceError error, String message) {
		super(WebConstants.REMOTE_SERVICE_ERROR_CODE, message);
		this.error = error;
	}

	/**
	 * 使用默认错误消息和原始异常创建异常实例
	 *
	 * @param error 远程服务错误信息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	public RemoteServiceException(RemoteServiceError error, Throwable cause) {
		super(WebConstants.REMOTE_SERVICE_ERROR_CODE, DEFAULT_MESSAGE, cause);
		this.error = error;
	}

	/**
	 * 使用自定义错误消息和原始异常创建异常实例
	 *
	 * @param error 远程服务错误信息
	 * @param message 自定义错误消息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	public RemoteServiceException(RemoteServiceError error, String message, Throwable cause) {
		super(WebConstants.REMOTE_SERVICE_ERROR_CODE, message, cause);
		this.error = error;
	}

	/**
	 * 使用自定义错误代码和消息创建异常实例
	 * <p>
	 * 受保护的构造方法，用于子类扩展。
	 * </p>
	 *
	 * @param error 远程服务错误信息
	 * @param code 自定义错误代码
	 * @param message 自定义错误消息
	 * @since 1.0.0
	 */
	protected RemoteServiceException(RemoteServiceError error, int code, String message) {
		super(code, message);
		this.error = error;
	}

	/**
	 * 使用自定义错误代码、消息和原始异常创建异常实例
	 * <p>
	 * 受保护的构造方法，用于子类扩展。
	 * </p>
	 *
	 * @param error 远程服务错误信息
	 * @param code 自定义错误代码
	 * @param message 自定义错误消息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	protected RemoteServiceException(RemoteServiceError error, int code, String message, Throwable cause) {
		super(code, message, cause);
		this.error = error;
	}

	/**
	 * 获取远程服务错误信息
	 *
	 * @return 远程服务错误信息对象
	 * @since 1.0.0
	 */
	public RemoteServiceError getError() {
		return error;
	}

	/**
	 * 使用默认日志级别（ERROR）记录异常信息
	 *
	 * @param logger 日志记录器
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger) {
		log(logger, Level.ERROR);
	}

	/**
	 * 使用指定日志级别记录异常信息
	 * <p>
	 * 记录的信息包括：
	 * <ul>
	 *     <li>服务名称（如果存在）</li>
	 *     <li>接口名称（如果存在）</li>
	 *     <li>请求URI（如果存在）</li>
	 *     <li>HTTP状态码、错误码和错误消息（通过{@link #generateRequestLog(StringBuilder)}生成）</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 日志格式示例：
	 * <pre>
	 * 服务：用户服务 接口：获取用户信息 链接：http://example.com/api/users http状态码：200 错误码：-1 错误信息：用户不存在
	 * </pre>
	 * </p>
	 *
	 * @param logger 日志记录器，如果为null则直接返回
	 * @param level 日志级别，如果为null则使用ERROR级别
	 * @see #generateRequestLog(StringBuilder)
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
		if (Objects.isNull(logger)) {
			return;
		}

		StringBuilder builder = new StringBuilder();
		if (StringUtils.isNotBlank(this.error.service())) {
			builder.append("服务：")
				.append(this.error.service())
				.append(StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(this.error.api())) {
			builder.append("接口：")
				.append(this.error.api())
				.append(StringUtils.SPACE);
		}
		if (Objects.nonNull(this.error.uri())) {
			builder.append("链接：")
				.append(this.error.uri())
				.append(StringUtils.SPACE);
		}
		generateRequestLog(builder);

		logger.atLevel(ObjectUtils.defaultIfNull(level, Level.ERROR))
			.setCause(this)
			.log(builder.toString());
	}

	/**
	 * 生成请求错误日志内容
	 * <p>
	 * 在现有的StringBuilder中追加错误相关信息，包括：
	 * <ul>
	 *     <li>HTTP状态码</li>
	 *     <li>错误码（如果存在）</li>
	 *     <li>错误消息（如果存在）</li>
	 * </ul>
	 * </p>
	 *
	 * @param builder 用于构建日志内容的StringBuilder实例
	 * @since 1.0.0
	 */
	protected void generateRequestLog(StringBuilder builder) {
		builder.append("http状态码：")
			.append(this.error.httpStatus())
			.append(StringUtils.SPACE);
		if (StringUtils.isNotBlank(this.error.code())) {
			builder.append("错误码：")
				.append(this.error.code())
				.append(StringUtils.SPACE);
		}
		if (StringUtils.isNotBlank(this.error.message())) {
			builder.append("错误信息：")
				.append(this.error.message());
		}
	}
}