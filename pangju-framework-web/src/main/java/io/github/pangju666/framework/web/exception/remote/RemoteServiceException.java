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
import io.github.pangju666.framework.web.pool.WebConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * 远程服务调用异常类（错误码为{@link WebConstants#REMOTE_SERVICE_ERROR_CODE}）
 * <p>
 * 该异常类用于处理远程服务调用过程中发生的异常情况，继承自{@link ServiceException}。
 * 提供了完整的远程服务错误信息封装，支持错误追踪和结构化日志记录。
 * </p>
 *
 * <p>
 * 主要特点：
 * <ul>
 *     <li>支持自定义错误消息和错误码</li>
 *     <li>包含完整的远程服务调用信息（服务名、接口名、URI等）</li>
 *     <li>提供结构化的日志记录功能</li>
 *     <li>支持异常链传递，便于问题追踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>微服务间调用异常：服务不可用、超时等</li>
 *     <li>远程API调用失败：HTTP请求失败、响应异常等</li>
 *     <li>第三方服务集成异常：外部服务异常、协议错误等</li>
 *     <li>RPC调用异常：序列化失败、网络中断等</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 基本使用
 * RemoteServiceError error = RemoteServiceError.builder()
 *     .service("用户服务")
 *     .api("获取用户信息")
 *     .uri("http://user-service/users/1")
 *     .httpStatus(404)
 *     .message("用户不存在")
 *     .build();
 * throw new RemoteServiceException(error);
 *
 * // 包含原始异常
 * try {
 *     // 远程服务调用
 * } catch (Exception e) {
 *     RemoteServiceError error = RemoteServiceError.builder()
 *         .service("订单服务")
 *         .api("创建订单")
 *         .build();
 *     throw new RemoteServiceException(error, "创建订单失败", e);
 * }
 * }</pre>
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

	/**
	 * 使用默认错误消息创建异常实例
	 *
	 * @param error 远程服务错误信息，包含服务名称、接口名称、URI等详细信息
	 * @throws NullPointerException 如果error参数为null
	 * @since 1.0.0
	 */
	public RemoteServiceException(RemoteServiceError error) {
		super(WebConstants.REMOTE_SERVICE_ERROR_CODE, REMOTE_ERROR_MESSAGE);
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
	protected RemoteServiceException(int code, RemoteServiceError error, String message) {
		super(code, message);
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