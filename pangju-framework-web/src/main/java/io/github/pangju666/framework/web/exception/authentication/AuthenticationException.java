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

package io.github.pangju666.framework.web.exception.authentication;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;
import io.github.pangju666.framework.web.exception.base.BaseHttpException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@HttpException(code = 0, type = HttpExceptionType.AUTHENTICATION, status = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends BaseHttpException {
	protected final String userIdentifiers;

	public AuthenticationException(String message, String userIdentifiers, String reason) {
		super(message, reason);
		this.userIdentifiers = userIdentifiers;
	}

	public AuthenticationException(String message, String userIdentifiers, String reason, Throwable cause) {
		super(message, reason, cause);
		this.userIdentifiers = userIdentifiers;
	}

	@Override
	public void log(Logger logger, Level level) {
		String message = String.format("认证错误，用户标识：%s，原因：%s",
			StringUtils.defaultIfBlank(this.userIdentifiers, "未知"),
			StringUtils.defaultIfBlank(this.reason, "未知"));
		logger.atLevel(level)
			.setCause(this)
			.log(message);
	}
}