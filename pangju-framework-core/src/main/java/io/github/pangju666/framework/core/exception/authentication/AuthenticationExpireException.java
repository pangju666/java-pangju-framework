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

package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.Constants;

public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException(String message) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	public AuthenticationExpireException(String message, String reason) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, Throwable cause) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, cause);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	public AuthenticationExpireException(String message, String reason, Throwable cause) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason, cause);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}
}