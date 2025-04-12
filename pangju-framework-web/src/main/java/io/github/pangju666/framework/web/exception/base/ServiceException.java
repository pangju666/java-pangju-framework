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

public class ServiceException extends BaseRuntimeException {
	private final String reason;

	public ServiceException(String message) {
		super(WebConstants.SERVICE_ERROR_CODE, message);
		this.reason = message;
	}

	public ServiceException(String message, String reason) {
		super(WebConstants.SERVICE_ERROR_CODE, message);
		this.reason = reason;
	}

	public ServiceException(String message, Throwable cause) {
		super(WebConstants.SERVICE_ERROR_CODE, message, cause);
		this.reason = message;
	}

	public ServiceException(String message, String reason, Throwable cause) {
		super(WebConstants.SERVICE_ERROR_CODE, message, cause);
		this.reason = reason;
	}

	protected ServiceException(int code, String message) {
		super(code, message);
		this.reason = message;
	}

	protected ServiceException(int code, String message, String reason) {
		super(code, message);
		this.reason = reason;
	}

	protected ServiceException(int code, String message, Throwable cause) {
		super(code, message, cause);
		this.reason = message;
	}

	protected ServiceException(int code, String message, String reason, Throwable cause) {
		super(code, message, cause);
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public void log(Logger logger) {
		logger.error(this.reason, this);
	}

	@Override
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.reason);
	}
}