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

import io.github.pangju666.framework.web.lang.pool.WebConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public abstract class BaseRuntimeException extends RuntimeException {
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

	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = this.getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return (rootCause != null ? rootCause : this);
	}

	public Throwable getMostSpecificCause() {
		return ObjectUtils.defaultIfNull(getRootCause(), this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof BaseRuntimeException e) {
			return e.contains(exType);
		} else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}
}