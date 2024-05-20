package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdNotExistException extends ValidationException {
	public IdNotExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "id不存在");
	}

	public IdNotExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdNotExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}
}
