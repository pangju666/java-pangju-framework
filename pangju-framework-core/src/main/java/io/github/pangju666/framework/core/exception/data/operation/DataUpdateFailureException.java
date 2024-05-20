package io.github.pangju666.framework.core.exception.data.operation;

public class DataUpdateFailureException extends DataOperationFailureException {
	public DataUpdateFailureException() {
		super("数据更新失败");
	}

	public DataUpdateFailureException(String message) {
		super(message);
	}

	public DataUpdateFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
