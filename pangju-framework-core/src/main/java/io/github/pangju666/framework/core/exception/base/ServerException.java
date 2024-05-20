package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class ServerException extends BaseRuntimeException {
	public ServerException() {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, "服务器内部错误");
	}

	public ServerException(String message) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, message);
	}

	public ServerException(Throwable cause) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, "服务器内部错误", cause);
	}

	public ServerException(String message, Throwable cause) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, message, cause);
	}
}
