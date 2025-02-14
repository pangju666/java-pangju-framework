package io.github.pangju666.framework.core.exception.data;

public class DataRemoveFailureException extends DataAccessException {
	public DataRemoveFailureException() {
		super("数据删除失败");
		this.setLog(false);
	}

	public DataRemoveFailureException(String reason) {
		super(reason);
	}

	public DataRemoveFailureException(Throwable cause) {
		super("数据删除失败", cause);
	}

	public DataRemoveFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
