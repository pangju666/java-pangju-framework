package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataCreateFailureException extends DataOperationFailureException {
	public DataCreateFailureException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据创建失败");
	}

	public DataCreateFailureException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataCreateFailureException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataCreateFailureException(int code, String message) {
		super(code, message);
	}

	protected DataCreateFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataCreateFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataCreateFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
