package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdNotExistException extends InvalidIdException {
	public IdNotExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "id不存在");
	}

	public IdNotExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdNotExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	protected IdNotExistException(int code, String message) {
		super(code, message);
	}

	protected IdNotExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected IdNotExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected IdNotExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
