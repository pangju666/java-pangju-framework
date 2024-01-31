package io.github.pangju666.framework.core.exception.data.operation;

public class DataUpdateFailureException extends DataOperationFailureException {
	public DataUpdateFailureException() {
		super(-51230, "数据更新失败");
	}

	public DataUpdateFailureException(String message) {
		super(-51230, message);
	}

	public DataUpdateFailureException(String message, Throwable cause) {
		super(-51230, message, cause);
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
