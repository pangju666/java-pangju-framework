package io.github.pangju666.framework.core.exception.data;

public class DataUpdateFailureException extends DataAccessException {
	public DataUpdateFailureException() {
		super("数据更新失败");
		this.setLog(false);
	}

	public DataUpdateFailureException(String reason) {
		super(reason);
	}

	public DataUpdateFailureException(Throwable cause) {
		super("数据更新失败", cause);
	}

	public DataUpdateFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}