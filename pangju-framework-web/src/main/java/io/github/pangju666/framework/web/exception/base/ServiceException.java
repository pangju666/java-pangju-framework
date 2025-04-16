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

/**
 * 业务逻辑异常
 * <p>
 * 用于处理业务逻辑相关的异常情况，如：
 * <ul>
 *     <li>业务规则验证失败</li>
 *     <li>业务流程异常</li>
 *     <li>业务状态错误</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：1000</li>
 *     <li>HTTP状态码：200 (OK)</li>
 *     <li>开启日志记录</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.SERVICE)
public class ServiceException extends BaseHttpException {
	public ServiceException(String message) {
		super(message, message);
	}

	public ServiceException(String message, String reason) {
		super(message, reason);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, message, cause);
	}

	public ServiceException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
	}
}