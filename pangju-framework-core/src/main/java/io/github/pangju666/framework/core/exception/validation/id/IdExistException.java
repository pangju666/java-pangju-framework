package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdExistException extends InvalidIdException {
	public IdExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "id已存在");
	}

	public IdExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	protected IdExistException(int code, String message) {
		super(code, message);
	}

	protected IdExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
