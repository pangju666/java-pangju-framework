package io.github.pangju666.framework.core.exception.data;

import io.github.pangju666.framework.core.exception.base.ServerException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataAccessException extends ServerException {
	public DataAccessException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据访问异常");
	}

	public DataAccessException(String reason) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, reason);
	}

	public DataAccessException(String reason, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, reason, cause);
	}

	protected DataAccessException(int code, String reason) {
		super(code, reason);
	}

	protected DataAccessException(int code, String reason, Throwable cause) {
		super(code, reason, cause);
	}
}
