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

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.ServiceException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

@HttpException(code = 100, description = "远程服务错误", type = HttpExceptionType.SERVICE)
public class RemoteServiceException extends ServiceException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未指定自定义错误消息时使用的默认消息文本。
	 * 此常量为protected以允许子类访问和复用。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String REMOTE_ERROR_MESSAGE = "远程服务请求失败";

	/**
	 * 远程服务错误信息
	 * <p>
	 * 存储远程服务调用的完整错误上下文信息，包括：
	 * <ul>
	 *     <li>服务名称：标识被调用的服务</li>
	 *     <li>接口名称：标识被调用的具体接口</li>
	 *     <li>请求URI：完整的请求地址</li>
	 *     <li>HTTP状态码：响应状态码</li>
	 *     <li>错误码：业务错误码</li>
	 *     <li>错误消息：详细错误描述</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 字段声明为final以确保异常信息的不可变性，提高线程安全性。
	 * 声明为protected以允许子类访问错误信息。
	 * </p>
	 *
	 * @see RemoteServiceError
	 * @since 1.0.0
	 */
	protected final RemoteServiceError error;

	public RemoteServiceException(RemoteServiceError error) {
		super(REMOTE_ERROR_MESSAGE);
		this.error = error;
	}

	public RemoteServiceException(String message, RemoteServiceError error) {
		super(message);
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