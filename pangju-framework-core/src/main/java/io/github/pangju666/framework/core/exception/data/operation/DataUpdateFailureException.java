package io.github.pangju666.framework.core.exception.data.operation;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataUpdateFailureException extends DataAccessException {
    public DataUpdateFailureException() {
        super("数据更新失败");
    }

    public DataUpdateFailureException(String message) {
        super(message);
    }

    public DataUpdateFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
