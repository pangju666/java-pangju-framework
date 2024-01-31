package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataOperationFailureException extends DataAccessException {
	public DataOperationFailureException() {
		super(-51200, "数据操作失败");
	}

	public DataOperationFailureException(String message) {
		super(-51200, message);
	}

	public DataOperationFailureException(String message, Throwable cause) {
		super(-51200, message, cause);
	}

	protected DataOperationFailureException(int code, String message) {
		super(code, message);
	}

	protected DataOperationFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataOperationFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataOperationFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
