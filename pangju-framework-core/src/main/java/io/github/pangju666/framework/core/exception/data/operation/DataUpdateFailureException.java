package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataUpdateFailureException extends DataOperationFailureException {
	public DataUpdateFailureException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据更新失败");
	}

	public DataUpdateFailureException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataUpdateFailureException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataUpdateFailureException(int code, String message) {
		super(code, message);
	}

	protected DataUpdateFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataUpdateFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataUpdateFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
