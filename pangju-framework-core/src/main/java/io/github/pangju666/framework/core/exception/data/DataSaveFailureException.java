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

package io.github.pangju666.framework.core.exception.data;

public class DataSaveFailureException extends DataAccessException {
	public DataSaveFailureException() {
		super("数据保存失败");
		this.setLog(false);
	}

	public DataSaveFailureException(String reason) {
		super(reason);
	}

	public DataSaveFailureException(Throwable cause) {
		super("数据保存失败", cause);
	}

	public DataSaveFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}