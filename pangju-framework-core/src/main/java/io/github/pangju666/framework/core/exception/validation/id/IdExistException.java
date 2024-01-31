package io.github.pangju666.framework.core.exception.validation.id;

public class IdExistException extends InvalidIdException {
	public IdExistException() {
		super(-43810, "id已存在");
	}

	public IdExistException(String message) {
		super(-43810, message);
	}

	public IdExistException(String message, Throwable cause) {
		super(-43810, message, cause);
	}

	protected IdExistException(int code, String message) {
		super(code, message);
	}

	protected IdExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
