package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataRemoveFailureException extends DataOperationFailureException {
	public DataRemoveFailureException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据删除失败");
	}

	public DataRemoveFailureException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataRemoveFailureException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataRemoveFailureException(int code, String message) {
		super(code, message);
	}

	protected DataRemoveFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataRemoveFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataRemoveFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
