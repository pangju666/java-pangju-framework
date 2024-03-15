package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public abstract class BaseRuntimeException extends RuntimeException {
	protected final int code;
	protected final int httpStatus;

	protected BaseRuntimeException() {
		super();
		this.code = ConstantPool.BASE_ERROR_RESPONSE_CODE;
		this.httpStatus = ConstantPool.OK_STATUS_code;
	}

	protected BaseRuntimeException(String message) {
		super(message);
		this.code = ConstantPool.BASE_ERROR_RESPONSE_CODE;
		this.httpStatus = ConstantPool.OK_STATUS_code;
	}

	protected BaseRuntimeException(Throwable cause) {
		super(cause);
		this.code = ConstantPool.BASE_ERROR_RESPONSE_CODE;
		this.httpStatus = ConstantPool.OK_STATUS_code;
	}

	protected BaseRuntimeException(int code, String message) {
		super(message);
		this.code = code;
		this.httpStatus = ConstantPool.OK_STATUS_code;
	}

	protected BaseRuntimeException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.httpStatus = ConstantPool.OK_STATUS_code;
	}

	protected BaseRuntimeException(int code, String message, int status) {
		super(message);
		this.httpStatus = status;
		this.code = code;
	}

	protected BaseRuntimeException(int code, String message, int status, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.httpStatus = status;
	}

	public int getCode() {
		return code;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		Throwable cause = getCause();
		String message = super.getMessage();
		if (cause == null) {
			return message;
		}
		StringBuilder sb = new StringBuilder(64);
		if (message != null) {
			sb.append(message).append("; ");
		}
		sb.append("base runtime exception is ").append(cause);
		return sb.toString();
	}

	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = this.getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return (rootCause != null ? rootCause : this);
	}

	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof BaseRuntimeException e) {
			return e.contains(exType);
		} else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}
}
