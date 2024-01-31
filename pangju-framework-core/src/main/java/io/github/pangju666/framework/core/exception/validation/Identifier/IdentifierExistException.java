package io.github.pangju666.framework.core.exception.validation.Identifier;

public class IdentifierExistException extends InvalidIdentifierException {
	public IdentifierExistException() {
		super(-43710, "标识符已存在");
	}

	public IdentifierExistException(String message) {
		super(-43710, message);
	}

	public IdentifierExistException(String message, Throwable cause) {
		super(-43710, message, cause);
	}

	protected IdentifierExistException(int code, String message) {
		super(code, message);
	}

	protected IdentifierExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdentifierExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdentifierExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
