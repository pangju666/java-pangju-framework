package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataQueryFailureException extends DataAccessException {
	public DataQueryFailureException() {
		super(-51100, "数据查询失败");
	}

	public DataQueryFailureException(String message) {
		super(-51100, message);
	}

	public DataQueryFailureException(String message, Throwable cause) {
		super(-51100, message, cause);
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
