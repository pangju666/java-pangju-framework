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
import org.springframework.http.HttpStatus;

/**
 * 服务器内部异常
 * <p>
 * 用于处理服务器内部错误和系统级异常，如：
 * <ul>
 *     <li>系统运行时异常</li>
 *     <li>资源访问失败</li>
 *     <li>系统配置错误</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：5000</li>
 *     <li>HTTP状态码：500 (Internal Server Error)</li>
 *     <li>错误消息：{@link #SERVER_ERROR_MESSAGE}</li>
 *     <li>开启日志记录</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.SERVER, status = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends BaseHttpException {
	public static final String SERVER_ERROR_MESSAGE = "服务器内部错误";

	public ServerException(String reason) {
		super(SERVER_ERROR_MESSAGE, reason);
	}

	public ServerException(String reason, Throwable cause) {
		super(SERVER_ERROR_MESSAGE, reason, cause);
	}
}