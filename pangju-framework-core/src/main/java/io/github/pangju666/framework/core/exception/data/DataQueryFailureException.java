package io.github.pangju666.framework.core.exception.data;

public class DataQueryFailureException extends DataAccessException {
	public DataQueryFailureException() {
		super("数据查询失败");
	}

	public DataQueryFailureException(String message) {
		super(message);
	}

	public DataQueryFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}