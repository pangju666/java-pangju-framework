package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataRecordExistException extends DataAccessException {
	public DataRecordExistException() {
		super("数据记录已存在");
	}

	public DataRecordExistException(String message) {
		super(message);
	}

	public DataRecordExistException(String message, Throwable cause) {
		super(message, cause);
	}
}
