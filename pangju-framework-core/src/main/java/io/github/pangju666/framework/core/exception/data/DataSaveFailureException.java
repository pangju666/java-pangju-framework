package io.github.pangju666.framework.core.exception.data;

public class DataSaveFailureException extends DataAccessException {
	public DataSaveFailureException() {
		super("数据保存失败");
		this.setLog(false);
	}

	public DataSaveFailureException(String reason) {
		super(reason);
	}

	public DataSaveFailureException(Throwable cause) {
		super("数据保存失败", cause);
	}

	public DataSaveFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}