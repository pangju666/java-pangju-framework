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
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * HTTP远程服务超时异常
 * <p>
 * 专门用于处理HTTP远程服务调用超时的场景，包括：
 * <ul>
 *     <li>连接超时：建立HTTP连接时超时</li>
 *     <li>请求超时：发送HTTP请求时超时</li>
 *     <li>响应超时：等待HTTP响应时超时</li>
 *     <li>处理超时：服务端处理HTTP请求时超时</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：1110（{@link HttpExceptionType#SERVICE} + 110）</li>
 *     <li>简化的日志记录格式，仅包含关键信息</li>
 *     <li>继承自{@link HttpRemoteServiceException}，复用HTTP错误处理逻辑</li>
 *     <li>专注于超时场景的异常处理</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建HTTP超时错误信息
 * HttpRemoteServiceError error = new HttpRemoteServiceError(
 *     "订单服务",                      // 服务名称
 *     "创建订单",                      // 接口名称
 *     "http://api.example.com/orders", // 请求URI
 * );
 *
 * // 抛出HTTP远程服务超时异常
 * throw new HttpRemoteServiceTimeoutException("订单服务响应超时", error);
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 110, description = "远程服务超时错误", type = HttpExceptionType.SERVICE)
public class HttpRemoteServiceTimeoutException extends HttpRemoteServiceException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未指定自定义错误消息时使用的标准超时错误提示。
	 * 声明为protected以允许子类复用此消息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String REMOTE_TIMEOUT_ERROR_MESSAGE = "远程服务请求超时";

	/**
	 * 创建HTTP远程服务超时异常实例（使用默认错误消息）
	 *
	 * @param error HTTP远程服务错误信息对象，包含服务调用的详细错误上下文
	 * @since 1.0.0
	 */
	public HttpRemoteServiceTimeoutException(HttpRemoteServiceError error) {
		super(REMOTE_TIMEOUT_ERROR_MESSAGE, error);
	}

	/**
	 * 创建HTTP远程服务超时异常实例（使用自定义错误消息）
	 *
	 * @param message 自定义错误消息，用于提供更具体的超时错误描述
	 * @param error   HTTP远程服务错误信息对象，包含服务调用的详细错误上下文
	 * @since 1.0.0
	 */
	public HttpRemoteServiceTimeoutException(String message, HttpRemoteServiceError error) {
		super(message, error);
	}

	/**
	 * 记录HTTP远程服务超时异常日志
	 * <p>
	 * 以简化的格式记录超时异常信息，仅包含：
	 * <ul>
	 *     <li>服务名称（默认为"未知"）</li>
	 *     <li>功能名称（默认为"未知"）</li>
	 *     <li>请求链接（默认为"未知"）</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 相比父类{@link HttpRemoteServiceException#log(Logger, Level)}，
	 * 省略了HTTP状态码、错误码等信息，使日志更加简洁。
	 * </p>
	 *
	 * @param logger 日志记录器
	 * @param level  日志级别
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("http远程服务请求超时，服务：%s，功能：%s，链接：%s",
			StringUtils.defaultIfBlank(this.error.service(), "未知"),
			StringUtils.defaultIfBlank(this.error.api(), "未知"),
			Objects.toString(this.error.uri(), "未知"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}