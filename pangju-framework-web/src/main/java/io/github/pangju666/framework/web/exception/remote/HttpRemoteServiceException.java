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
import io.github.pangju666.framework.web.model.error.HttpRemoteServiceError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * HTTP远程服务异常
 * <p>
 * 用于处理基于HTTP协议的远程服务调用异常，包括：
 * <ul>
 *     <li>HTTP请求失败：无法建立连接、请求超时等</li>
 *     <li>HTTP响应异常：4xx客户端错误、5xx服务器错误等</li>
 *     <li>业务处理失败：返回业务错误码和错误消息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：1100（{@link HttpExceptionType#SERVICE} + 100）</li>
 *     <li>支持HTTP协议特定的错误信息记录</li>
 *     <li>提供结构化的日志输出格式</li>
 *     <li>支持完整的HTTP请求上下文追踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 创建HTTP错误信息对象
 * HttpRemoteServiceError error = new HttpRemoteServiceError(
 *     "用户服务",                    // 服务名称
 *     "获取用户信息",                // 接口名称
 *     "http://api.example.com/users", // 请求URI
 *     HttpStatus.NOT_FOUND,          // HTTP状态码
 *     "USER-404",                   // 错误码
 *     "用户不存在"                   // 错误消息
 * );
 *
 * // 抛出HTTP远程服务异常
 * throw new HttpRemoteServiceException("调用用户服务失败", error);
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 100, description = "http远程服务错误", type = HttpExceptionType.SERVICE)
public class HttpRemoteServiceException extends ServiceException {
	/**
	 * 默认错误消息
	 * <p>
	 * 当未指定自定义错误消息时使用的标准错误提示。
	 * 声明为protected以允许子类复用此消息。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String REMOTE_ERROR_MESSAGE = "远程服务请求失败";

	/**
	 * 远程服务错误信息
	 * <p>
	 * 存储HTTP远程服务调用的完整错误上下文信息，包括：
	 * <ul>
	 *     <li>服务名称：标识被调用的服务</li>
	 *     <li>接口名称：标识被调用的具体接口</li>
	 *     <li>请求URI：完整的HTTP请求地址</li>
	 *     <li>HTTP状态码：标准HTTP响应状态码</li>
	 *     <li>错误码：业务层面的错误编码</li>
	 *     <li>错误消息：具体的错误描述信息</li>
	 * </ul>
	 * </p>
	 *
	 * @see HttpRemoteServiceError
	 * @since 1.0.0
	 */
	protected final HttpRemoteServiceError error;

	/**
	 * 创建HTTP远程服务异常实例（使用默认错误消息）
	 *
	 * @param error HTTP远程服务错误信息对象，包含完整的错误上下文
	 * @since 1.0.0
	 */
	public HttpRemoteServiceException(HttpRemoteServiceError error) {
		super(REMOTE_ERROR_MESSAGE);
		this.error = error;
	}

	/**
	 * 创建HTTP远程服务异常实例（使用自定义错误消息）
	 *
	 * @param message 自定义错误消息，用于提供更具体的错误描述
	 * @param error   HTTP远程服务错误信息对象，包含完整的错误上下文
	 * @since 1.0.0
	 */
	public HttpRemoteServiceException(String message, HttpRemoteServiceError error) {
		super(message);
		this.error = error;
	}

	/**
	 * 获取HTTP远程服务错误信息
	 *
	 * @return HTTP远程服务错误信息对象，包含完整的错误上下文
	 * @since 1.0.0
	 */
	public HttpRemoteServiceError getError() {
		return error;
	}

	/**
	 * 记录HTTP远程服务异常日志
	 * <p>
	 * 以结构化的格式记录异常信息，包括：
	 * <ul>
	 *     <li>服务名称（默认为"未知"）</li>
	 *     <li>功能名称（默认为"未知"）</li>
	 *     <li>请求链接（默认为"未知"）</li>
	 *     <li>HTTP状态码（默认为"未知"）</li>
	 *     <li>错误码（默认为"无"）</li>
	 *     <li>错误消息（默认为"无"）</li>
	 * </ul>
	 * </p>
	 *
	 * @param logger 日志记录器
	 * @param level 日志级别
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("http远程服务请求失败，服务：%s，功能：%s，链接：%s，http状态码：%s 错误码：%s 错误信息：%s",
			StringUtils.defaultIfBlank(this.error.service(), "未知"),
			StringUtils.defaultIfBlank(this.error.api(), "未知"),
			Objects.toString(this.error.uri(), "未知"),
			Objects.nonNull(this.error.httpStatus()) ? this.error.httpStatus().value() : "未知",
			StringUtils.defaultIfBlank(this.error.code(), "无"),
			StringUtils.defaultIfBlank(this.error.message(), "无"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}