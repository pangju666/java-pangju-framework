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
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@HttpException(code = 100, description = "认证过期错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.UNAUTHORIZED, log = false)
public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException(String message) {
		super(message, null, null);
	}

	@Override
	public void log(Logger logger) {
	}

	@Override
	public void log(Logger logger, Level level) {
	}
}