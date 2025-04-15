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

import io.github.pangju666.framework.web.pool.WebConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataQueryFailureException extends DataOperationException {
	public static final String DATA_QUERY_ERROR_MESSAGE = "数据创建失败";

	public DataQueryFailureException(String reason) {
		super(WebConstants.DATA_ERROR_CODE, DATA_QUERY_ERROR_MESSAGE, reason);
	}

	public DataQueryFailureException(String reason, Throwable cause) {
		super(WebConstants.DATA_ERROR_CODE, DATA_QUERY_ERROR_MESSAGE, reason, cause);
	}

	protected DataQueryFailureException(int code, String message, String reason) {
		super(code, message, reason);
	}

	protected DataQueryFailureException(int code, String message, String reason, Throwable cause) {
		super(code, message, reason, cause);
	}
}