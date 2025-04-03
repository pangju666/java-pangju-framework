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

package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.Constants;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ServerException extends BaseRuntimeException {
	protected static final String DEFAULT_MESSAGE = "服务器内部错误";

	private final String reason;

	public ServerException() {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
		this.reason = DEFAULT_MESSAGE;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(Throwable cause) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
		this.reason = DEFAULT_MESSAGE;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason, Throwable cause) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	protected ServerException(int code, String reason) {
		super(code, DEFAULT_MESSAGE);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	protected ServerException(int code, String reason, Throwable cause) {
		super(code, DEFAULT_MESSAGE, cause);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
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