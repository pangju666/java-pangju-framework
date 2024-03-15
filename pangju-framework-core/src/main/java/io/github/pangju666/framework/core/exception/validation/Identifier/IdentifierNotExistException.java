package io.github.pangju666.framework.core.exception.validation.Identifier;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdentifierNotExistException extends InvalidIdentifierException {
	public IdentifierNotExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "标识符不存在");
	}

	public IdentifierNotExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdentifierNotExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	protected IdentifierNotExistException(int code, String message) {
		super(code, message);
	}

	protected IdentifierNotExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdentifierNotExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdentifierNotExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
