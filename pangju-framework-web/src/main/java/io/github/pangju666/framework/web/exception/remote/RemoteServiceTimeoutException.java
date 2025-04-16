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

@HttpException(code = 110, description = "远程服务超时错误", type = HttpExceptionType.SERVICE)
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

	public RemoteServiceTimeoutException(RemoteServiceError error) {
		super(REMOTE_TIMEOUT_ERROR_MESSAGE, error);
	}

	public RemoteServiceTimeoutException(String message, RemoteServiceError error) {
		super(message, error);
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