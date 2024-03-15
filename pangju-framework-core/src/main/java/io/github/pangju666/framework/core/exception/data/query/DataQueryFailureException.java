package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.exception.data.DataAccessException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataQueryFailureException extends DataAccessException {
	public DataQueryFailureException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据查询失败");
	}

	public DataQueryFailureException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataQueryFailureException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataQueryFailureException(int code, String message) {
		super(code, message);
	}

	protected DataQueryFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataQueryFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataQueryFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
