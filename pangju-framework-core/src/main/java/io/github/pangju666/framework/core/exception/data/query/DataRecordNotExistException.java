package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataRecordNotExistException extends DataQueryFailureException {
	public DataRecordNotExistException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据记录不存在");
	}

	public DataRecordNotExistException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataRecordNotExistException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataRecordNotExistException(int code, String message) {
		super(code, message);
	}

	protected DataRecordNotExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataRecordNotExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataRecordNotExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
