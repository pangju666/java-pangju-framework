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

import io.github.pangju666.framework.web.pool.WebConstants;

/**
 * 远程服务调用超时异常类（错误码为{@link WebConstants#REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE}）
 * <p>
 * 该异常类专门用于处理远程服务调用超时的场景，继承自{@link RemoteServiceException}，并提供了简化的日志记录格式。
 * </p>
 *
 * <p>
 * 主要特点：
 * <ul>
 *     <li>专用超时错误码：使用独立的超时错误码，便于区分其他远程服务异常</li>
 *     <li>简化日志格式：仅记录核心超时信息，避免冗余日志</li>
 *     <li>默认超时提示：提供统一的超时错误提示信息</li>
 *     <li>支持自定义：允许自定义错误码和错误消息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>HTTP请求超时：远程API调用超过预设时间</li>
 *     <li>RPC调用超时：微服务间通信超时</li>
 *     <li>数据库操作超时：远程数据库查询或事务超时</li>
 *     <li>第三方服务超时：外部服务集成超时</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 基本使用 - HTTP请求超时
 * try {
 *     restTemplate.getForEntity(url, Response.class);
 * } catch (RestClientTimeoutException e) {
 *     RemoteServiceError error = RemoteServiceError.builder()
 *         .service("用户服务")
 *         .api("获取用户信息")
 *         .uri(url)
 *         .build();
 *     throw new RemoteServiceTimeoutException(error);
 * }
 *
 * // 自定义错误消息 - RPC调用超时
 * try {
 *     userService.getUserInfo(userId);
 * } catch (TimeoutException e) {
 *     RemoteServiceError error = RemoteServiceError.builder()
 *         .service("用户服务")
 *         .api("getUserInfo")
 *         .build();
 *     throw new RemoteServiceTimeoutException(error, "获取用户信息超时，请稍后重试");
 * }
 * }</pre>
 * </p>
 *
 * @author pangju666
 * @see RemoteServiceException 远程服务异常类
 * @see RemoteServiceError 远程服务错误信息
 * @since 1.0.0
 */
public class RemoteServiceTimeoutException extends RemoteServiceException {
	/**
	 * 默认超时错误消息
	 * <p>
	 * 定义统一的超时错误提示信息，用于向用户展示友好的错误提示。
	 * 声明为protected以允许子类访问和复用。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	protected static final String REMOTE_TIMEOUT_ERROR_MESSAGE = "远程服务请求超时";

	/**
	 * 使用默认错误消息创建超时异常实例
	 *
	 * @param error 远程服务错误信息，包含服务名称、接口名称、URI等详细信息
	 * @throws IllegalArgumentException 如果error参数为null
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError error) {
		super(WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, error, REMOTE_TIMEOUT_ERROR_MESSAGE);
	}

	/**
	 * 使用自定义错误消息创建超时异常实例
	 *
	 * @param error 远程服务错误信息，包含服务名称、接口名称、URI等详细信息
	 * @param message 自定义错误消息，用于向用户展示更友好的超时提示
	 * @throws IllegalArgumentException 如果error参数为null
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError error, String message) {
		super(WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, error, message);
	}

	/**
	 * 使用自定义错误代码和消息创建超时异常实例
	 * <p>
	 * 此构造方法为protected访问级别，主要用于子类扩展。允许完全自定义错误码和错误消息，
	 * 适用于需要特殊超时错误码处理的场景。
	 * </p>
	 *
	 * @param error 远程服务错误信息，包含服务名称、接口名称、URI等详细信息
	 * @param code 自定义错误代码，用于标识特定类型的超时错误
	 * @param message 自定义错误消息，用于向用户展示更友好的超时提示
	 * @throws NullPointerException 如果error参数为null
	 * @since 1.0.0
	 */
	protected RemoteServiceTimeoutException(RemoteServiceError error, int code, String message) {
		super(code, error, message);
	}

	/**
	 * 生成超时请求的日志内容
	 * <p>
	 * 重写父类的日志生成方法，提供简化的超时信息记录格式。与父类的详细错误信息不同，
	 * 此方法仅记录"请求超时"的简单说明，避免在日志中产生冗余信息。
	 * </p>
	 *
	 * <p>
	 * 日志示例：
	 * <pre>
	 * 服务：用户服务 接口：获取用户信息 链接：http://example.com/api/users 请求超时
	 * </pre>
	 * </p>
	 *
	 * @param builder 用于构建日志内容的StringBuilder实例
	 * @see RemoteServiceException#generateRequestLog(StringBuilder)
	 * @since 1.0.0
	 */
	@Override
	protected void generateRequestLog(StringBuilder builder) {
		builder.append("请求超时");
	}
}