package io.github.pangju666.framework.core.exception.validation.Identifier;

public class IdentifierNotExistException extends InvalidIdentifierException {
	public IdentifierNotExistException() {
		super(-43720, "标识符不存在");
	}

	public IdentifierNotExistException(String message) {
		super(-43720, message);
	}

	public IdentifierNotExistException(String message, Throwable cause) {
		super(-43720, message, cause);
	}

	protected IdentifierNotExistException(int code, String message) {
		super(code, message);
	}

	protected IdentifierNotExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdentifierNotExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdentifierNotExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
