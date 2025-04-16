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
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

/**
 * 参数校验异常
 * <p>
 * 用于处理请求参数验证相关的异常情况，如：
 * <ul>
 *     <li>必填参数缺失</li>
 *     <li>参数格式错误</li>
 *     <li>参数值范围错误</li>
 * </ul>
 * </p>
 *
 * <p>
 * 特点：
 * <ul>
 *     <li>错误码：4000</li>
 *     <li>HTTP状态码：400 (Bad Request)</li>
 *     <li>关闭日志记录</li>
 * </ul>
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@HttpException(code = 0, type = HttpExceptionType.VALIDATION, log = false, status = HttpStatus.BAD_REQUEST)
public class ValidationException extends BaseHttpException {
	public ValidationException(String message) {
		super(message, message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, message, cause);
	}

	/**
	 * 重写父类的日志记录方法
	 * <p>
	 * 由于验证异常通常不需要记录日志，此方法实现为空。
	 * </p>
	 *
	 * @param logger 日志记录器（此处不使用）
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger) {
	}

	/**
	 * 重写父类的日志记录方法
	 * <p>
	 * 由于验证异常通常不需要记录日志，此方法实现为空。
	 * </p>
	 *
	 * @param logger 日志记录器（此处不使用）
	 * @param level 日志级别（此处不使用）
	 * @since 1.0.0
	 */
	@Override
	public void log(Logger logger, Level level) {
	}
}