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
 * 权限不足异常类
 * <p>
 * 该异常类用于表示用户缺少所需操作权限的情况，继承自{@link AuthenticationException}。
 * 当抛出此异常时，将自动返回HTTP 403 (Forbidden) 状态码。
 * 该类使用{@link IgnoreLog}注解标记，表示不需要记录日志。
 * </p>
 *
 * <p>
 * 主要特点：
 * <ul>
 *     <li>自动映射为HTTP 403状态码</li>
 *     <li>默认不记录日志</li>
 *     <li>专门用于处理权限不足的场景</li>
 *     <li>错误消息直接反馈给用户</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>操作权限检查：用户尝试执行未授权的操作</li>
 *     <li>资源访问控制：用户尝试访问未授权的资源</li>
 *     <li>功能权限验证：用户尝试使用未授权的功能</li>
 *     <li>数据权限控制：用户尝试访问未授权的数据</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 操作权限检查
 * if (!user.hasPermission("user:delete")) {
 *     throw new NoPermissionException("没有删除用户的权限");
 * }
 *
 * // 资源访问检查
 * if (!user.hasPermission("file:read:" + fileId)) {
 *     throw new NoPermissionException("没有查看该文件的权限");
 * }
 *
 * // 数据权限检查
 * if (!user.hasPermission("data:sensitive")) {
 *     throw new NoPermissionException("没有访问敏感数据的权限");
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionException extends AuthenticationException {
	/**
	 * 构造一个权限不足异常
	 *
	 * @param message 错误消息，用于向用户展示权限不足的具体原因
	 * @since 1.0.0
	 */
	public NoPermissionException(String message) {
		super(WebConstants.AUTHENTICATION_ERROR_CODE, message);
	}

	/**
	 * 构造一个权限不足异常，包含原始异常
	 *
	 * @param message 错误消息，用于向用户展示权限不足的具体原因
	 * @param cause 导致此异常的原始异常，用于异常链追踪
	 * @since 1.0.0
	 */
	public NoPermissionException(String message, Throwable cause) {
		super(WebConstants.AUTHENTICATION_ERROR_CODE, message, cause);
	}

	/**
	 * 构造一个权限不足异常，使用自定义错误码
	 * <p>
	 * 此构造方法受保护，主要用于子类扩展，允许指定自定义错误码。
	 * </p>
	 *
	 * @param code 自定义错误码，用于标识特定类型的权限错误
	 * @param message 错误消息，用于向用户展示权限不足的具体原因
	 * @since 1.0.0
	 */
	protected NoPermissionException(int code, String message) {
		super(code, message);
	}

	/**
	 * 构造一个权限不足异常，使用自定义错误码并包含原始异常
	 * <p>
	 * 此构造方法受保护，主要用于子类扩展，允许指定自定义错误码和原始异常。
	 * </p>
	 *
	 * @param code 自定义错误码，用于标识特定类型的权限错误
	 * @param message 错误消息，用于向用户展示权限不足的具体原因
	 * @param cause 导致此异常的原始异常，用于异常链追踪
	 * @since 1.0.0
	 */
	protected NoPermissionException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}