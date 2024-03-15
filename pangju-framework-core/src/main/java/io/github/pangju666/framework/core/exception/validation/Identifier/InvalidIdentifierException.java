package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.validation.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class InvalidIdentifierException extends ValidationException {
	public InvalidIdentifierException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "不是合法的标识符");
	}

	public InvalidIdentifierException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public InvalidIdentifierException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	protected InvalidIdentifierException(int code, String message) {
		super(code, message);
	}

	protected InvalidIdentifierException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected InvalidIdentifierException(int code, String message, int status) {
		super(code, message, status);
	}

	protected InvalidIdentifierException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
