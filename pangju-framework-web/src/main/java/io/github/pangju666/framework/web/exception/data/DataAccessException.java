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

package io.github.pangju666.framework.web.exception.data;

import io.github.pangju666.framework.web.annotation.IgnoreLog;
import io.github.pangju666.framework.web.exception.base.ServerException;
import io.github.pangju666.framework.web.pool.WebConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@IgnoreLog
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataAccessException extends ServerException {
	public DataAccessException() {
		super(WebConstants.DATA_ERROR_CODE, "数据访问异常");
	}

	public DataAccessException(String reason) {
		super(WebConstants.DATA_ERROR_CODE, reason);
	}

	public DataAccessException(Throwable cause) {
		super(WebConstants.DATA_ERROR_CODE, "数据访问异常", cause);
	}

	public DataAccessException(String reason, Throwable cause) {
		super(WebConstants.DATA_ERROR_CODE, reason, cause);
	}

	protected DataAccessException(int code, String reason) {
		super(code, reason);
	}

	protected DataAccessException(int code, String reason, Throwable cause) {
		super(code, reason, cause);
	}
}