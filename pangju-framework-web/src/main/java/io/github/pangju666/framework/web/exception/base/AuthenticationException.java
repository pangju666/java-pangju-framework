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

package io.github.pangju666.framework.web.exception.base;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

/**
 * 认证授权异常
 * <p>
 * 用于处理认证和授权过程中的异常情况，如：
 * <ul>
 *     <li>用户未登录：未提供认证信息</li>
 *     <li>认证失败：用户名或密码错误</li>
 *     <li>会话过期：Token失效或过期</li>
 *     <li>权限不足：无访问特定资源的权限</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>基础错误码：-3000（{@link HttpExceptionType#AUTHENTICATION}）</li>
 *     <li>HTTP状态码：401（{@link HttpStatus#UNAUTHORIZED}）</li>
 *     <li>不记录日志</li>
 *     <li>支持多种类型的用户标识</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 用户未登录
 * if (token == null) {
 *     throw new AuthenticationException(
 *         "请先登录",           // 展示消息
 *         "anonymous",        // 用户标识
 *         "未提供Token"        // 错误原因
 *     );
 * }
 *
 * // Token验证失败
 * try {
 *     tokenService.validate(token);
 * } catch (TokenException e) {
 *     throw new AuthenticationException(
 *         "登录已过期",         // 展示消息
 *         userId,            // 用户标识（支持各种类型）
 *         "Token已过期",      // 错误原因
 *         e                  // 原始异常
 *     );
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.AUTHENTICATION, description = "认证授权错误", status = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends BaseHttpException {
	/**
	 * 用户标识信息
	 * <p>
	 * 用于记录发生认证异常的用户信息，支持多种类型：
	 * <ul>
	 *     <li>String：用户名、Token等字符串标识</li>
	 *     <li>Number：用户ID等数值标识</li>
	 *     <li>Object：复杂的用户标识对象</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected final Object userIdentifier;

	/**
	 * 创建认证异常实例
	 *
	 * @param message        展示给用户的错误消息
	 * @param userIdentifier 用户标识信息（支持任意类型）
	 * @param reason         错误原因，用于日志记录
	 * @since 1.0.0
	 */
	public AuthenticationException(String message, Object userIdentifier, String reason) {
		super(message, reason);
		this.userIdentifier = userIdentifier;
	}

	/**
	 * 创建带有原因异常的认证异常实例
	 *
	 * @param message        展示给用户的错误消息
	 * @param userIdentifier 用户标识信息（字符串类型）
	 * @param reason         错误原因，用于日志记录
	 * @param cause          导致此异常的原始异常
	 * @since 1.0.0
	 */
	public AuthenticationException(String message, String userIdentifier, String reason, Throwable cause) {
		super(message, reason, cause);
		this.userIdentifier = userIdentifier;
	}

	/**
	 * 重写日志记录方法
	 * <p>
	 * 提供认证异常的结构化日志信息，包含：
	 * <ul>
	 *     <li>用户标识：通过{@link #valueToString}方法转换的用户标识信息</li>
	 *     <li>错误原因：认证失败的具体原因</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * 日志格式：
	 * <pre>
	 * 认证错误，用户标识：[userIdentifier]，原因：[reason]
	 * </pre>
	 * 当字段为空时显示"未知"
	 * </p>
	 *
	 * @param logger 用于记录日志的Logger实例
	 * @param level  日志记录的级别
	 * @see #valueToString(Object, String)
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("认证授权错误，用户标识：%s，原因：%s",
			valueToString(this.userIdentifier, "未知"),
			StringUtils.defaultIfBlank(this.reason, "未知"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}