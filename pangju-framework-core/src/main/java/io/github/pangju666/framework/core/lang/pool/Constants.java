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

package io.github.pangju666.framework.core.lang.pool;

public class Constants extends io.github.pangju666.commons.lang.pool.Constants {
	// Redis相关常量
	public static final String REDIS_PATH_DELIMITER = "::";

	// 鉴权相关常量
	public static final String ADMIN_ROLE = "admin";

	// HTTP常量
	public static final int OK_HTTP_STATUS_CODE = 200;
	public static final int BAD_REQUEST_HTTP_STATUS_CODE = 400;
	public static final int UNAUTHORIZED_HTTP_STATUS_CODE = 401;
	public static final int FORBIDDEN_HTTP_STATUS_CODE = 403;
	public static final int INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE = 500;

	// 响应信息相关常量
	public static final String RESPONSE_SUCCESS_MESSAGE = "请求成功";
	public static final String RESPONSE_FAILURE_MESSAGE = "请求失败";

	// 响应状态码相关常量
	public static final int SUCCESS_RESPONSE_CODE = 0;
	public static final int BASE_ERROR_RESPONSE_CODE = -1;
	public static final int VALIDATION_ERROR_RESPONSE_CODE = -30000;
	public static final int DATA_ERROR_RESPONSE_CODE = -51000;
	public static final int SERVER_ERROR_RESPONSE_CODE = -50000;
	public static final int SERVICE_ERROR_RESPONSE_CODE = -40000;
	public static final int AUTHENTICATION_ERROR_RESPONSE_CODE = -42000;
	public static final int AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE = -42100;
	public static final int REMOTE_SERVICE_ERROR_RESPONSE_CODE = -44000;
	public static final int REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE = -44100;

	protected Constants() {
	}
}