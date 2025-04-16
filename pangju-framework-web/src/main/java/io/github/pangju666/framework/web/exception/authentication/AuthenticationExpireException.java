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

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.AuthenticationException;
import org.springframework.http.HttpStatus;

/**
 * 认证过期异常
 * <p>
 * 用于处理用户认证信息过期的情况：
 * <ul>
 *     <li>会话超时：用户会话已过期</li>
 *     <li>Token过期：访问令牌已失效</li>
 *     <li>凭证过期：登录凭证需要更新</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：3100（{@link HttpExceptionType#AUTHENTICATION} + 100）</li>
 *     <li>HTTP状态码：401（{@link HttpStatus#UNAUTHORIZED}）</li>
 *     <li>不记录日志</li>
 *     <li>仅需提供错误消息</li>
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
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 100, description = "认证过期错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.UNAUTHORIZED, log = false)
public class AuthenticationExpireException extends AuthenticationException {
	/**
	 * 创建认证过期异常实例
	 *
	 * @param message 展示给用户的错误消息（如："登录已过期，请重新登录"）
	 * @since 1.0.0
	 */
	public AuthenticationExpireException(String message) {
		super(message, null, null);
	}
}