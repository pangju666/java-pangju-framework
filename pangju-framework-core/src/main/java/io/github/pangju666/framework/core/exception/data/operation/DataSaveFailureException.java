package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataSaveFailureException extends DataOperationFailureException {
	public DataSaveFailureException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据保存失败");
	}

	public DataSaveFailureException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataSaveFailureException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataSaveFailureException(int code, String message) {
		super(code, message);
	}

	protected DataSaveFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataSaveFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataSaveFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
