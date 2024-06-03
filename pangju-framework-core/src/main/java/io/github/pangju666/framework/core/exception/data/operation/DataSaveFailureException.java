package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataSaveFailureException extends DataAccessException {
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
