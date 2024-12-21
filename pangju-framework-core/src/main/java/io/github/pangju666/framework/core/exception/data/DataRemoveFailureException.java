package io.github.pangju666.framework.core.exception.data;

public class DataRemoveFailureException extends DataAccessException {
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
