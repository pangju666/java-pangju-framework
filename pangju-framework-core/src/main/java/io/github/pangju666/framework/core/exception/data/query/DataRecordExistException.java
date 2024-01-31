package io.github.pangju666.framework.core.exception.data.query;

public class DataRecordExistException extends DataQueryFailureException {
	public DataRecordExistException() {
		super(-51110, "数据记录已存在");
	}

	public DataRecordExistException(String message) {
		super(-51110, message);
	}

	public DataRecordExistException(String message, Throwable cause) {
		super(-51110, message, cause);
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
