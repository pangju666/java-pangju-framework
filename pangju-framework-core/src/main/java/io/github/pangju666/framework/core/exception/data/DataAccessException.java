package io.github.pangju666.framework.core.exception.data;

import io.github.pangju666.framework.core.exception.base.ServerException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataAccessException extends ServerException {
	public DataAccessException() {
		super("数据访问异常");
		setCode(ConstantPool.DATA_ERROR_RESPONSE_CODE);
	}

	public DataAccessException(String message) {
		super(message);
		setCode(ConstantPool.DATA_ERROR_RESPONSE_CODE);
	}

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
		setCode(ConstantPool.DATA_ERROR_RESPONSE_CODE);
	}
}
