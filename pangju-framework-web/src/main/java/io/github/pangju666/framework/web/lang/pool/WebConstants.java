package io.github.pangju666.framework.web.lang.pool;

public class WebConstants {
	// 鉴权相关常量
	public static final String ADMIN_ROLE = "admin";
	// 接口请求结果
	public static final String RESULT_SUCCESS_MESSAGE = "请求成功";
	public static final String RESULT_FAILURE_MESSAGE = "请求失败";
	// 异常代码
	public static final int SUCCESS_CODE = 0;
	public static final int BASE_ERROR_CODE = -1;
	public static final int AUTHENTICATION_ERROR_CODE = -42000;
	public static final int AUTHENTICATION_EXPIRE_ERROR_CODE = -42100;
	public static final int REMOTE_SERVICE_ERROR_CODE = -44000;
	public static final int REMOTE_SERVICE_TIMEOUT_RESPONSE_CODE = -44100;
	public static final int VALIDATION_ERROR_CODE = -30000;
	public static final int DATA_ERROR_CODE = -51000;
	public static final int SERVER_ERROR_CODE = -50000;
	public static final int SERVICE_ERROR_CODE = -40000;

	protected WebConstants() {
	}
}
