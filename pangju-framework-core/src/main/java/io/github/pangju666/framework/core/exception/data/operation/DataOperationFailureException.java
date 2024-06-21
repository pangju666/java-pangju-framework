package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataOperationFailureException extends DataAccessException {
    public DataOperationFailureException() {
        super("数据操作失败");
    }

    public DataOperationFailureException(String message) {
        super(message);
    }

    public DataOperationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
