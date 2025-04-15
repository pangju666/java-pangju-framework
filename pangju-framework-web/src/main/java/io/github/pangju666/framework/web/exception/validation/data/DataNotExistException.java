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

package io.github.pangju666.framework.web.exception.validation.data;

import io.github.pangju666.framework.web.exception.base.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataNotExistException extends ValidationException {
	public DataNotExistException() {
		super("数据不存在");
	}

	public DataNotExistException(String message) {
		super(message);
	}

	public DataNotExistException(Throwable cause) {
		super("数据不存在", cause);
	}

	public DataNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}