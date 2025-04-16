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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Objects;

@HttpException(code = 200, description = "缺少角色错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.FORBIDDEN, log = false)
public class NoRoleException extends AuthenticationException {
	public NoRoleException(String message) {
		super(message, null, null);
	}

	public NoRoleException(String... roles) {
		super(roles.length == 0 ? "缺少相应角色" : (roles.length == 1 ? "缺少" + roles[0] + "角色" :
			"至少需要" + StringUtils.join(roles, "、") + "中任一角色"), null, null);
	}

	public NoRoleException(Collection<String> roles) {
		super(Objects.isNull(roles) ? "缺少相应角色" : (roles.size() == 1 ? "缺少" +
			roles.iterator().next() + "角色" : "至少需要" + StringUtils.join(roles, "、") +
			"中任一角色"), null, null);
	}

	@Override
	public void log(Logger logger) {
	}

	@Override
	public void log(Logger logger, Level level) {
	}
}