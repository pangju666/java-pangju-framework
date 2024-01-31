package io.github.pangju666.framework.core.exception.data.operation;

public class DataRemoveFailureException extends DataOperationFailureException {
	public DataRemoveFailureException() {
		super(-51220, "数据删除失败");
	}

	public DataRemoveFailureException(String message) {
		super(-51220, message);
	}

	public DataRemoveFailureException(String message, Throwable cause) {
		super(-51220, message, cause);
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
