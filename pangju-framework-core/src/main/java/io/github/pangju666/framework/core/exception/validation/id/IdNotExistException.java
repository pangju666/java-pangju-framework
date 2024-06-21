package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;

public class IdNotExistException extends ValidationException {
    public IdNotExistException() {
        super("id不存在");
    }

    public IdNotExistException(String message) {
        super(message);
    }

    public IdNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
