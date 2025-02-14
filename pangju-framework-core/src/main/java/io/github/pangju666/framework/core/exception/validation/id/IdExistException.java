package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdExistException extends ValidationException {
	public IdExistException() {
		super("id已存在");
	}

	public IdExistException(String message) {
		super(message);
	}

	public IdExistException(Throwable cause) {
		super("id已存在", cause);
	}

	public IdExistException(String message, Throwable cause) {
		super(message, cause);
	}
}