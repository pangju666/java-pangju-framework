package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class InvalidIdException extends ValidationException {
	public InvalidIdException() {
		super("不是合法的id");
	}

	public InvalidIdException(String message) {
		super(message);
	}

	public InvalidIdException(String message, Throwable cause) {
		super(message, cause);
	}
}