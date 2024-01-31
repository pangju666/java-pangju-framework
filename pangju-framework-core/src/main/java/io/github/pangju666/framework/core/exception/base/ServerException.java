package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class ServerException extends BaseRuntimeException {
	public ServerException() {
		super(ConstantPool.ERROR_SERVER_CODE, "服务器内部错误");
	}

	public ServerException(String message) {
		super(ConstantPool.ERROR_SERVER_CODE, message);
	}

	public ServerException(String message, Throwable cause) {
		super(ConstantPool.ERROR_SERVER_CODE, message, cause);
	}

	protected ServerException(int code, String message) {
		super(code, message);
	}

	protected ServerException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected ServerException(int code, String message, int status) {
		super(code, message, status);
	}

	protected ServerException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
