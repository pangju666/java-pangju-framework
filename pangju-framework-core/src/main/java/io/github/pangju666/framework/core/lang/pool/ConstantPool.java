package io.github.pangju666.framework.core.lang.pool;

public class ConstantPool extends io.github.pangju666.commons.lang.pool.ConstantPool {
	// Redis相关常量
	public static final String REDIS_PATH_DELIMITER = "::";

	// 鉴权相关常量
	public static final String ADMIN_ROLE = "admin";

	// HTTP常量
	public static final int OK_STATUS_code = 200;

	// 响应信息相关常量
	public static final String RESPONSE_SUCCESS_MESSAGE = "请求成功";
	public static final String RESPONSE_FAILURE_MESSAGE = "请求失败";

	// 响应状态码相关常量
	public static final Integer SUCCESS_RESPONSE_CODE = 0;
	public static final Integer BASE_ERROR_RESPONSE_CODE = -1;
	public static final Integer VALIDATION_ERROR_RESPONSE_CODE = -43000;
	public static final Integer DATA_ERROR_RESPONSE_CODE = -51000;
	public static final Integer SERVER_ERROR_RESPONSE_CODE = -50000;
	public static final Integer SERVICE_ERROR_RESPONSE_CODE = -40000;
	public static final Integer SECURITY_ERROR_RESPONSE_CODE = -42000;
	public static final Integer REMOTE_ERROR_RESPONSE_CODE = -44000;

	protected ConstantPool() {
	}
}