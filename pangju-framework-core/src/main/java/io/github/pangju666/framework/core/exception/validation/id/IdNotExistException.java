package io.github.pangju666.framework.core.exception.validation.id;

public class IdNotExistException extends InvalidIdException {
	public IdNotExistException() {
		super(-43820, "id不存在");
	}

	public IdNotExistException(String message) {
		super(-43820, message);
	}

	public IdNotExistException(String message, Throwable cause) {
		super(-43820, message, cause);
	}

	protected IdNotExistException(int code, String message) {
		super(code, message);
	}

	protected IdNotExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdNotExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdNotExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
