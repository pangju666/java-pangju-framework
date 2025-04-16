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
 * 角色缺失异常
 * <p>
 * 用于处理用户访问需要特定角色的资源时的异常情况：
 * <ul>
 *     <li>单个角色缺失：用户不具备访问资源所需的指定角色</li>
 *     <li>多个角色缺失：用户不具备多个可选角色中的任意一个</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：3200（{@link HttpExceptionType#AUTHENTICATION} + 200）</li>
 *     <li>HTTP状态码：403（{@link HttpStatus#FORBIDDEN}）</li>
 *     <li>不记录日志</li>
 *     <li>支持多种角色检查方式</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 单个角色检查
 * if (!hasRole("admin")) {
 *     throw new NoRoleException("admin");
 * }
 *
 * // 多个角色检查（任一满足即可）
 * if (!hasAnyRole("admin", "manager")) {
 *     throw new NoRoleException("admin", "manager");
 * }
 *
 * // 自定义错误消息
 * throw new NoRoleException("需要管理员角色");
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 200, description = "缺少角色错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.FORBIDDEN, log = false)
public class NoRoleException extends AuthenticationException {
	/**
	 * 创建角色缺失异常实例（自定义消息）
	 *
	 * @param message 展示给用户的自定义错误消息
	 * @since 1.0.0
	 */
	public NoRoleException(String message) {
		super(message, null, null);
	}

	/**
	 * 创建角色缺失异常实例（可变参数）
	 * <p>
	 * 根据角色数量自动生成错误消息：
	 * <ul>
	 *     <li>无角色：缺少相应角色</li>
	 *     <li>单个角色：缺少[role]角色</li>
	 *     <li>多个角色：至少需要[role1]、[role2]中任一角色</li>
	 * </ul>
	 * </p>
	 *
	 * @param roles 缺失的角色列表（可变参数）
	 * @since 1.0.0
	 */
	public NoRoleException(String... roles) {
		super(roles.length == 0 ? "缺少相应角色" : (roles.length == 1 ? "缺少" + roles[0] + "角色" :
			"至少需要" + StringUtils.join(roles, "、") + "中任一角色"), null, null);
	}

	/**
	 * 创建角色缺失异常实例（集合参数）
	 * <p>
	 * 根据角色集合自动生成错误消息：
	 * <ul>
	 *     <li>空集合：缺少相应角色</li>
	 *     <li>单个角色：缺少[role]角色</li>
	 *     <li>多个角色：至少需要[role1]、[role2]中任一角色</li>
	 * </ul>
	 * </p>
	 *
	 * @param roles 缺失的角色集合
	 * @since 1.0.0
	 */
	public NoRoleException(Collection<String> roles) {
		super(Objects.isNull(roles) ? "缺少相应角色" : (roles.size() == 1 ? "缺少" +
			roles.iterator().next() + "角色" : "至少需要" + StringUtils.join(roles, "、") +
			"中任一角色"), null, null);
	}
}