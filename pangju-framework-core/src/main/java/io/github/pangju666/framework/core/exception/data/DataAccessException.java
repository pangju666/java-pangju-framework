package io.github.pangju666.framework.core.exception.data;


import io.github.pangju666.framework.core.exception.base.ServerException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataAccessException extends ServerException {
	public DataAccessException() {
		super(ConstantPool.ERROR_DATABASE_CODE, "数据访问异常");
	}

	public DataAccessException(String message) {
		super(ConstantPool.ERROR_DATABASE_CODE, message);
	}

	public DataAccessException(String message, Throwable cause) {
		super(ConstantPool.ERROR_DATABASE_CODE, message, cause);
	}

	protected DataAccessException(int code, String message) {
		super(code, message);
	}

	protected DataAccessException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataAccessException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataAccessException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
