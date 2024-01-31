package io.github.pangju666.framework.core.lang.pool;

public class ConstantPool extends io.github.pangju666.commons.lang.pool.ConstantPool {
	// 接口相关常量
	public static final Integer RESPONSE_SUCCESS_CODE = 0;
	public static final Integer RESPONSE_FAILURE_CODE = -1;
	public static final String RESPONSE_SUCCESS_MESSAGE = "请求成功";
	public static final String RESPONSE_FAILURE_MESSAGE = "请求失败";

	// Redis相关常量
	public static final String REDIS_PATH_DELIMITER = "::";

	// 鉴权相关常量
	public static final String ADMIN_ROLE = "admin";

	// 加解密相关常量
	public static final String DEFAULT_AES_TRANSFORMATION = "AES/ECB/PKCS5padding";

	// Spring Bean相关常量
	public static final String DEFAULT_REDIS_TEMPLATE_BEAN_NAME = "redisTemplate";
	public static final String IO_THREAD_POOL_TASK_EXECUTOR_BEAN_NAME = "cpuApplicationTaskExecutor";
	public static final String CPU_THREAD_POOL_TASK_EXECUTOR_BEAN_NAME = "ioApplicationTaskExecutor";

	// 异常状态码相关常量
	public static final Integer ERROR_BASE_CODE = -1;
	public static final Integer ERROR_VALIDATION_CODE = -43000;
	public static final Integer ERROR_DATABASE_CODE = -51000;
	public static final Integer ERROR_SERVER_CODE = -50000;
	public static final Integer ERROR_SERVICE_CODE = -40000;
	public static final Integer ERROR_SECURITY_CODE = -42000;
	public static final Integer ERROR_REMOTE_CODE = -44000;

	protected ConstantPool() {
	}
}