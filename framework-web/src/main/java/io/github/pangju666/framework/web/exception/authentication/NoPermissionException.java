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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Objects;

/**
 * 权限缺失异常
 * <p>
 * 用于处理用户访问需要特定权限的资源时的异常情况：
 * <ul>
 *     <li>单个权限缺失：用户缺少访问资源所需的指定权限</li>
 *     <li>多个权限缺失：用户缺少多个可选权限中的任意一个</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：-3300（{@link HttpExceptionType#AUTHENTICATION} + 300）</li>
 *     <li>HTTP状态码：403（{@link HttpStatus#FORBIDDEN}）</li>
 *     <li>不记录日志</li>
 *     <li>支持多种权限检查方式</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 单个权限检查
 * if (!hasPermission("user:view")) {
 *     throw new NoPermissionException("user:view");
 * }
 *
 * // 多个权限检查（任一满足即可）
 * if (!hasAnyPermission("user:view", "user:edit")) {
 *     throw new NoPermissionException("user:view", "user:edit");
 * }
 *
 * // 自定义错误消息
 * throw new NoPermissionException("没有查看用户的权限");
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 300, description = "缺少权限错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.FORBIDDEN, log = false)
public class NoPermissionException extends AuthenticationException {
	/**
	 * 创建权限缺失异常实例（自定义消息）
	 *
	 * @param message 展示给用户的自定义错误消息
	 * @since 1.0.0
	 */
	public NoPermissionException(String message) {
		super(message, null, null);
	}

	/**
	 * 创建权限缺失异常实例（可变参数）
	 * <p>
	 * 根据权限数量自动生成错误消息：
	 * <ul>
	 *     <li>无权限：缺少相应权限</li>
	 *     <li>单个权限：缺少[permission]权限</li>
	 *     <li>多个权限：至少需要[permission1]、[permission2]中任一权限</li>
	 * </ul>
	 * </p>
	 *
	 * @param permissions 缺失的权限列表（可变参数）
	 * @since 1.0.0
	 */
	public NoPermissionException(String... permissions) {
		super(permissions.length == 0 ? "缺少相应权限" : (permissions.length == 1 ? "缺少 " + permissions[0] + " 权限" :
			"至少需要 " + StringUtils.join(permissions, "、") + " 中任一权限"), null, null);
	}

	/**
	 * 创建权限缺失异常实例（集合参数）
	 * <p>
	 * 根据权限集合自动生成错误消息：
	 * <ul>
	 *     <li>空集合：缺少相应权限</li>
	 *     <li>单个权限：缺少[permission]权限</li>
	 *     <li>多个权限：至少需要[permission1]、[permission2]中任一权限</li>
	 * </ul>
	 * </p>
	 *
	 * @param permissions 缺失的权限集合
	 * @since 1.0.0
	 */
	public NoPermissionException(Collection<String> permissions) {
		super(Objects.isNull(permissions) ? "缺少相应权限" : (permissions.size() == 1 ? "缺少" +
			permissions.iterator().next() + "权限" : "至少需要" + StringUtils.join(permissions, "、") +
			"中任一权限"), null, null);
	}
}