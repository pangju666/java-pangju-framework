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

import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.core.NestedRuntimeException;

public abstract class BaseHttpException extends NestedRuntimeException {
	protected final String reason;

	protected BaseHttpException(String message, String reason) {
		super(message);
		this.reason = reason;
	}

	protected BaseHttpException(String message, String reason, Throwable cause) {
		super(message, cause);
		this.reason = reason;
	}

	/**
	 * 使用ERROR级别记录异常日志
	 * <p>
	 * 此方法会将异常信息和堆栈跟踪记录到指定的日志记录器中
	 * </p>
	 *
	 * @param logger 用于记录日志的SLF4J日志记录器
	 * @since 1.0.0
	 */
	public void log(Logger logger) {
		logger.error(this.reason, this);
	}

	/**
	 * 使用指定级别记录异常日志
	 * <p>
	 * 此方法允许指定日志级别，适用于不同严重程度的异常情况
	 * </p>
	 *
	 * @param logger 用于记录日志的SLF4J日志记录器
	 * @param level 日志级别，如{@link Level#ERROR}、{@link Level#WARN}等
	 * @since 1.0.0
	 */
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.reason);
	}
}