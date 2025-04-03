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

package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdExistException extends ValidationException {
	public IdExistException() {
		super("id已存在");
	}

	public IdExistException(String message) {
		super(message);
	}

	public IdExistException(Throwable cause) {
		super("id已存在", cause);
	}

	public IdExistException(String message, Throwable cause) {
		super(message, cause);
	}
}