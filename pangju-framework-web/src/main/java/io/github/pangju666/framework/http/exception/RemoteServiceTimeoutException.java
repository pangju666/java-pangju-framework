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

package io.github.pangju666.framework.http.exception;

import io.github.pangju666.framework.http.model.RemoteServiceError;
import io.github.pangju666.framework.web.lang.pool.WebConstants;

/**
 * 远程服务调用超时异常
 * <p>
 * 专门用于处理远程服务调用超时的场景。此异常继承自{@link RemoteServiceException}，
 * 使用特定的超时错误码和简化的日志记录格式。
 * </p>
 *
 * <p>
 * 特性：
 * <ul>
 *     <li>使用专门的超时错误码{@link WebConstants#REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE}</li>
 *     <li>提供简化的超时日志记录格式</li>
 *     <li>保持与基础远程服务异常的兼容性</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @see RemoteServiceException
 * @since 1.0.0
 */
public class RemoteServiceTimeoutException extends RemoteServiceException {
	/**
	 * 默认超时错误消息
	 *
	 * @since 1.0.0
	 */
	protected static final String DEFAULT_MESSAGE = "远程服务请求超时";

	/**
	 * 使用默认错误消息创建超时异常实例
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError) {
		super(remoteServiceError, WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, DEFAULT_MESSAGE);
	}

	/**
	 * 使用自定义错误消息创建超时异常实例
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @param message 自定义错误消息
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, String message) {
		super(remoteServiceError, WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, message);
	}

	/**
	 * 使用默认错误消息和原始异常创建超时异常实例
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, Throwable cause) {
		super(remoteServiceError, WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
	}

	/**
	 * 使用自定义错误消息和原始异常创建超时异常实例
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @param message 自定义错误消息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, String message, Throwable cause) {
		super(remoteServiceError, WebConstants.REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE, message, cause);
	}

	/**
	 * 使用自定义错误代码和消息创建超时异常实例
	 * <p>
	 * 受保护的构造方法，用于子类扩展。
	 * </p>
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @param code 自定义错误代码
	 * @param message 自定义错误消息
	 * @since 1.0.0
	 */
	protected RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, int code, String message) {
		super(remoteServiceError, code, message);
	}

	/**
	 * 使用自定义错误代码、消息和原始异常创建超时异常实例
	 * <p>
	 * 受保护的构造方法，用于子类扩展。
	 * </p>
	 *
	 * @param remoteServiceError 远程服务错误信息
	 * @param code 自定义错误代码
	 * @param message 自定义错误消息
	 * @param cause 原始异常
	 * @since 1.0.0
	 */
	protected RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, int code, String message, Throwable cause) {
		super(remoteServiceError, code, message, cause);
	}

	/**
	 * 生成超时请求的日志内容
	 * <p>
	 * 重写父类的日志生成方法，提供简化的超时信息。
	 * 不同于父类的详细错误信息，此方法仅添加"请求超时"的简单说明。
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