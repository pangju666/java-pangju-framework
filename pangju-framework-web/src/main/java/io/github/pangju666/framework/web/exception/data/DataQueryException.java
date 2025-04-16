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

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.HttpExceptionType;

@HttpException(code = 100, description = DataQueryException.ERROR_MESSAGE, type = HttpExceptionType.DATA)
public class DataQueryException extends DataOperationException {
	public static final String ERROR_MESSAGE = "数据查询错误";
	protected static final String OPERATION = "创建";

	public DataQueryException(String source, String data, String reason) {
		super(ERROR_MESSAGE, source, OPERATION, data, reason);
	}

	public DataQueryException(String source, String data, String reason, Throwable cause) {
		super(ERROR_MESSAGE, source, OPERATION, data, reason, cause);
	}

	public DataQueryException(String message, String source, String data, String reason) {
		super(message, source, OPERATION, data, reason);
	}

	public DataQueryException(String message, String source, String data, String reason, Throwable cause) {
		super(message, source, OPERATION, reason, data, cause);
	}
}