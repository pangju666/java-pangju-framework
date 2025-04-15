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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataUpdateFailureException extends DataOperationException {
	public DataUpdateFailureException() {
		super("数据更新失败");
	}

	public DataUpdateFailureException(String reason) {
		super(reason);
	}

	public DataUpdateFailureException(Throwable cause) {
		super("数据更新失败", cause);
	}

	public DataUpdateFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}