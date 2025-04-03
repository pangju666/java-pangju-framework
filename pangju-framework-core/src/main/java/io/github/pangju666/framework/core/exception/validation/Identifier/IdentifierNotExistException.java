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

package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdentifierNotExistException extends ValidationException {
	public IdentifierNotExistException() {
		super("标识符不存在");
	}

	public IdentifierNotExistException(String message) {
		super(message);
	}

	public IdentifierNotExistException(Throwable cause) {
		super("标识符不存在", cause);
	}

	public IdentifierNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}