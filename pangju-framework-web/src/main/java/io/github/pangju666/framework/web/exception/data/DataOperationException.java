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

import io.github.pangju666.framework.web.exception.base.ServiceException;
import io.github.pangju666.framework.web.pool.WebConstants;

public class DataOperationException extends ServiceException {
	public static final String DATA_OPERATION_ERROR_MESSAGE = "数据操作失败";

	public DataOperationException(String reason) {
		super(WebConstants.DATA_ERROR_CODE, DATA_OPERATION_ERROR_MESSAGE, reason);
	}

	public DataOperationException(String reason, Throwable cause) {
		super(WebConstants.DATA_ERROR_CODE, DATA_OPERATION_ERROR_MESSAGE, reason, cause);
	}

	public DataOperationException(String message, String reason) {
		super(WebConstants.DATA_ERROR_CODE, message, reason);
	}

	public DataOperationException(String message, String reason, Throwable cause) {
		super(WebConstants.DATA_ERROR_CODE, message, reason, cause);
	}

	protected DataOperationException(int code, String message, String reason) {
		super(code, message, reason);
	}

	protected DataOperationException(int code, String message, String reason, Throwable cause) {
		super(code, message, reason, cause);
	}
}