package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;
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
}
