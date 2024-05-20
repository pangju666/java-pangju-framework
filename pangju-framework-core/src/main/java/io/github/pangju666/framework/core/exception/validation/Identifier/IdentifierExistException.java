package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdentifierExistException extends ValidationException {
	public IdentifierExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "标识符已存在");
	}

	public IdentifierExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdentifierExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}
}
