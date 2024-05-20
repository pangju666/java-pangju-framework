package io.github.pangju666.framework.core.exception.data.operation;

public class DataSaveFailureException extends DataOperationFailureException {
	public DataSaveFailureException() {
		super("数据保存失败");
	}

	public DataSaveFailureException(String message) {
		super(message);
	}

	public DataSaveFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
