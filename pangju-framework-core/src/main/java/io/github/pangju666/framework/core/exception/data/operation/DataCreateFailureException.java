package io.github.pangju666.framework.core.exception.data.operation;

public class DataCreateFailureException extends DataOperationFailureException {
	public DataCreateFailureException() {
		super(-51210, "数据创建失败");
	}

	public DataCreateFailureException(String message) {
		super(-51210, message);
	}

	public DataCreateFailureException(String message, Throwable cause) {
		super(-51210, message, cause);
	}

	protected DataCreateFailureException(int code, String message) {
		super(code, message);
	}

	protected DataCreateFailureException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected DataCreateFailureException(int code, String message, int status) {
		super(code, message, status);
	}

	protected DataCreateFailureException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
