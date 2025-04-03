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
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends BaseRuntimeException {
	protected static final String DEFAULT_MESSAGE = "服务器内部错误";

	private final String reason;

	public ServerException() {
		super(WebConstants.SERVER_ERROR_CODE, DEFAULT_MESSAGE);
		this.reason = DEFAULT_MESSAGE;
	}

	public ServerException(String reason) {
		super(WebConstants.SERVER_ERROR_CODE, DEFAULT_MESSAGE);
		this.reason = reason;
	}

	public ServerException(Throwable cause) {
		super(WebConstants.SERVER_ERROR_CODE, DEFAULT_MESSAGE, cause);
		this.reason = DEFAULT_MESSAGE;
	}

	public ServerException(String reason, Throwable cause) {
		super(WebConstants.SERVER_ERROR_CODE, DEFAULT_MESSAGE, cause);
		this.reason = reason;
	}

	protected ServerException(int code, String reason) {
		super(code, DEFAULT_MESSAGE);
		this.reason = reason;
	}

	protected ServerException(int code, String reason, Throwable cause) {
		super(code, DEFAULT_MESSAGE, cause);
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