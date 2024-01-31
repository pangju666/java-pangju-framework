package io.github.pangju666.framework.core.exception.data.query;

public class DataRecordNotExistException extends DataQueryFailureException {
	public DataRecordNotExistException() {
		super(-51120, "数据记录不存在");
	}

	public DataRecordNotExistException(String message) {
		super(-51120, message);
	}

	public DataRecordNotExistException(String message, Throwable cause) {
		super(-51120, message, cause);
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
