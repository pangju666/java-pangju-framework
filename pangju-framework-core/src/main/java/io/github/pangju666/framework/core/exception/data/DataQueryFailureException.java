package io.github.pangju666.framework.core.exception.data;

public class DataQueryFailureException extends DataAccessException {
	public DataQueryFailureException() {
		super("数据查询失败");
		this.setLog(false);
	}

	public DataQueryFailureException(String reason) {
		super(reason);
	}

	public DataQueryFailureException(Throwable cause) {
		super("数据查询失败", cause);
	}

	public DataQueryFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}