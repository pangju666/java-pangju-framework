package io.github.pangju666.framework.core.exception.data;

public class DataCreateFailureException extends DataAccessException {
	public DataCreateFailureException() {
		super("数据创建失败");
		this.setLog(false);
	}

	public DataCreateFailureException(String reason) {
		super(reason);
	}

	public DataCreateFailureException(Throwable cause) {
		super("数据创建失败", cause);
	}

	public DataCreateFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}