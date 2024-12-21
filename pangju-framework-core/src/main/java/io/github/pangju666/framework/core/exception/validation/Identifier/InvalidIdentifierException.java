package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class InvalidIdentifierException extends ValidationException {
	public InvalidIdentifierException() {
		super("不是合法的标识符");
	}

	public InvalidIdentifierException(String message) {
		super(message);
	}

	public InvalidIdentifierException(String message, Throwable cause) {
		super(message, cause);
	}
}