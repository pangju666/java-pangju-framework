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

import io.github.pangju666.framework.web.pool.WebConstants;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.core.NestedRuntimeException;

public abstract class BaseRuntimeException extends NestedRuntimeException {
	private int code = WebConstants.BASE_ERROR_CODE;

	protected BaseRuntimeException(String message) {
		super(message);
	}

	protected BaseRuntimeException(int code, String message) {
		super(message);
		this.code = code;
	}

	protected BaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	protected BaseRuntimeException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void log(Logger logger) {
		logger.error(this.getMessage(), this);
	}

	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.getMessage());
	}
}