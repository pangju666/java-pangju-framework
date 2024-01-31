package io.github.pangju666.framework.core.exception.data.operation;

public class DataSaveFailureException extends DataOperationFailureException {
	public DataSaveFailureException() {
		super(-51240, "数据保存失败");
	}

	public DataSaveFailureException(String message) {
		super(-51240, message);
	}

	public DataSaveFailureException(String message, Throwable cause) {
		super(-51240, message, cause);
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
