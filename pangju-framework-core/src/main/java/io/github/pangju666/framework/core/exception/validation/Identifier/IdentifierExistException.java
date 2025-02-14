package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdentifierExistException extends ValidationException {
	public IdentifierExistException() {
		super("标识符已存在");
	}

	public IdentifierExistException(String message) {
		super(message);
	}

	public IdentifierExistException(Throwable cause) {
		super("标识符已存在", cause);
	}

	public IdentifierExistException(String message, Throwable cause) {
		super(message, cause);
	}
}