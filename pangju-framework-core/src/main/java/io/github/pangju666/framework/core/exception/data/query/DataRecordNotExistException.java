package io.github.pangju666.framework.core.exception.data.query;

import io.github.pangju666.framework.core.exception.data.DataAccessException;

public class DataRecordNotExistException extends DataAccessException {
    public DataRecordNotExistException() {
        super("数据记录不存在");
    }

    public DataRecordNotExistException(String message) {
        super(message);
    }

    public DataRecordNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
