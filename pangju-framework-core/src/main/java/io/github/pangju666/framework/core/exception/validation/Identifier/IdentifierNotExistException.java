package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.exception.base.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdentifierNotExistException extends ValidationException {
	public IdentifierNotExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "标识符不存在");
	}

	public IdentifierNotExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdentifierNotExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}
}
