package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

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
