package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdentifierNotExistException extends ValidationException {
	public IdentifierNotExistException() {
		super("标识符不存在");
	}

	public IdentifierNotExistException(String message) {
		super(message);
	}

	public IdentifierNotExistException(Throwable cause) {
		super("标识符不存在", cause);
	}

	public IdentifierNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}