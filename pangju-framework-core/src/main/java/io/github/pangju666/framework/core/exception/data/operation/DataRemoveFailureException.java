package io.github.pangju666.framework.core.exception.data.operation;

public class DataRemoveFailureException extends DataOperationFailureException {
	public DataRemoveFailureException() {
		super("数据删除失败");
	}

	public DataRemoveFailureException(String message) {
		super(message);
	}

	public DataRemoveFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
