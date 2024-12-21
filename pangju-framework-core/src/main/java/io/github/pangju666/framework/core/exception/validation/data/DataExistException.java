package io.github.pangju666.framework.core.exception.validation.data;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class DataExistException extends ValidationException {
	public DataExistException() {
		super("数据已存在");
	}

	public DataExistException(String message) {
		super(message);
	}

	public DataExistException(String message, Throwable cause) {
		super(message, cause);
	}
}