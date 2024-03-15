package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdentifierExistException extends InvalidIdentifierException {
	public IdentifierExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "标识符已存在");
	}

	public IdentifierExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdentifierExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
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
