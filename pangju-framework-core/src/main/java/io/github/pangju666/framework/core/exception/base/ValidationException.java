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

public class ValidationException extends BaseRuntimeException {
	public ValidationException(String message) {
		super(Constants.VALIDATION_ERROR_RESPONSE_CODE, message);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	public ValidationException(String message, Throwable cause) {
		super(Constants.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	protected ValidationException(int code, String message) {
		super(code, message);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	protected ValidationException(int code, String message, Throwable cause) {
		super(code, message, cause);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	@Override
	public void log(Logger logger) {
	}

	@Override
	public void log(Logger logger, Level level) {
	}
}