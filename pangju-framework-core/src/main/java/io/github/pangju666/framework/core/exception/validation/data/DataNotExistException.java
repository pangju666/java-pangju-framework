package io.github.pangju666.framework.core.exception.validation.data;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class DataNotExistException extends ValidationException {
	public DataNotExistException() {
		super("数据不存在");
	}

	public DataNotExistException(String message) {
		super(message);
	}

	public DataNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}