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

package io.github.pangju666.framework.web.exception.authentication;

import io.github.pangju666.framework.web.exception.base.AuthenticationException;
import io.github.pangju666.framework.web.pool.WebConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 认证过期异常类（错误码为{@link WebConstants#AUTHENTICATION_EXPIRE_ERROR_CODE}）
 * <p>
 * 该异常类用于表示认证信息已过期的情况，继承自{@link AuthenticationException}。
 * 当抛出此异常时，将自动返回HTTP 401 (Unauthorized) 状态码。
 * 该类使用{@link IgnoreLog}注解标记，表示不需要记录日志。
 * </p>
 *
 * <p>
 * 主要特点：
 * <ul>
 *     <li>自动映射为HTTP 401状态码</li>
 *     <li>默认不记录日志</li>
 *     <li>专门用于处理认证过期的场景</li>
 *     <li>错误消息直接反馈给用户</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>登录会话过期：用户会话超时需要重新登录</li>
 *     <li>Token过期：如JWT token已过期</li>
 *     <li>认证凭证过期：如密码已过期需要修改</li>
 *     <li>临时认证失效：如临时访问令牌过期</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // Token过期检查
 * if (token.isExpired()) {
 *     throw new AuthenticationExpireException("登录已过期，请重新登录");
 * }
 *
 * // 会话超时检查
 * if (session.isExpired()) {
 *     throw new AuthenticationExpireException("会话已超时，请重新登录");
 * }
 *
 * // 密码过期检查
 * if (user.isPasswordExpired()) {
 *     throw new AuthenticationExpireException("密码已过期，请修改密码");
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationExpireException extends AuthenticationException {
	/**
	 * 构造一个认证过期异常
	 *
	 * @param message 错误消息，用于向用户展示认证过期的具体原因
	 * @since 1.0.0
	 */
	public AuthenticationExpireException(String message) {
		super(WebConstants.AUTHENTICATION_EXPIRE_ERROR_CODE, message);
	}

	/**
	 * 构造一个认证过期异常，包含原始异常
	 *
	 * @param message 错误消息，用于向用户展示认证过期的具体原因
	 * @param cause 导致此异常的原始异常，用于异常链追踪
	 * @since 1.0.0
	 */
	public AuthenticationExpireException(String message, Throwable cause) {
		super(WebConstants.AUTHENTICATION_EXPIRE_ERROR_CODE, message, cause);
	}

	/**
	 * 构造一个认证过期异常，使用自定义错误码
	 * <p>
	 * 此构造方法受保护，主要用于子类扩展，允许指定自定义错误码。
	 * </p>
	 *
	 * @param code 自定义错误码，用于标识特定类型的认证过期错误
	 * @param message 错误消息，用于向用户展示认证过期的具体原因
	 * @since 1.0.0
	 */
	protected AuthenticationExpireException(int code, String message) {
		super(code, message);
	}

	/**
	 * 构造一个认证过期异常，使用自定义错误码并包含原始异常
	 * <p>
	 * 此构造方法受保护，主要用于子类扩展，允许指定自定义错误码和原始异常。
	 * </p>
	 *
	 * @param code 自定义错误码，用于标识特定类型的认证过期错误
	 * @param message 错误消息，用于向用户展示认证过期的具体原因
	 * @param cause 导致此异常的原始异常，用于异常链追踪
	 * @since 1.0.0
	 */
	protected AuthenticationExpireException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}