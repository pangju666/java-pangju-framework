package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.validation.ValidationException;

public class InvalidIdException extends ValidationException {
	public InvalidIdException() {
		super(-43800, "不是合法的id");
	}

	public InvalidIdException(String message) {
		super(-43800, message);
	}

	public InvalidIdException(String message, Throwable cause) {
		super(-43800, message, cause);
	}

	protected InvalidIdException(int code, String message) {
		super(code, message);
	}

	protected InvalidIdException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected InvalidIdException(int code, String message, int status) {
		super(code, message, status);
	}

	protected InvalidIdException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
