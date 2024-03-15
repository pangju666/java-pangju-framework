package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class DataRecordExistException extends DataQueryFailureException {
	public DataRecordExistException() {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, "数据记录已存在");
	}

	public DataRecordExistException(String message) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message);
	}

	public DataRecordExistException(String message, Throwable cause) {
		super(ConstantPool.DATA_ERROR_RESPONSE_CODE, message, cause);
	}

	protected DataRecordExistException(int code, String message) {
		super(code, message);
	}

	protected DataRecordExistException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataRecordExistException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataRecordExistException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
