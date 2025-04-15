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

package io.github.pangju666.framework.web.exception.base;

import io.github.pangju666.framework.web.annotation.HttpException;
import io.github.pangju666.framework.web.enums.ExceptionType;

@HttpException(code = 2000, type = ExceptionType.DATA)
public class DataException extends BaseHttpException {
	public DataException(String message) {
		super(message, message);
	}

	public DataException(String message, String reason) {
		super(message, reason);
	}

	public DataException(String message, Throwable cause) {
		super(message, message, cause);
	}

	public DataException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
	}
}