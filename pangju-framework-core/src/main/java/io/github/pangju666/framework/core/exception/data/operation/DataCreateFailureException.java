package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataCreateFailureException extends DataAccessException {
    public DataCreateFailureException() {
        super("数据创建失败");
    }

    public DataCreateFailureException(String message) {
        super(message);
    }

    public DataCreateFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
