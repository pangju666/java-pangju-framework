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

@HttpException(code = 300, description = "缺少权限错误", type = HttpExceptionType.AUTHENTICATION,
	status = HttpStatus.FORBIDDEN, log = false)
public class NoPermissionException extends AuthenticationException {
	public NoPermissionException(String message) {
		super(message, null, null);
	}

	public NoPermissionException(String... permissions) {
		super(permissions.length == 0 ? "缺少相应权限" : (permissions.length == 1 ? "缺少" + permissions[0] + "权限" :
			"至少需要" + StringUtils.join(permissions, "、") + "中任一权限"), null, null);
	}

	public NoPermissionException(Collection<String> permissions) {
		super(Objects.isNull(permissions) ? "缺少相应权限" : (permissions.size() == 1 ? "缺少" +
			permissions.iterator().next() + "权限" : "至少需要" + StringUtils.join(permissions, "、") +
			"中任一权限"), null, null);
	}

	@Override
	public void log(Logger logger) {
	}

	@Override
	public void log(Logger logger, Level level) {
	}
}