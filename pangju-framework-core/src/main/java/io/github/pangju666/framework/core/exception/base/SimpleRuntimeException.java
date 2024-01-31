package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

import java.util.function.Function;

/**
 * 框架异常简单实现，内部使用
 */
public final class SimpleRuntimeException extends BaseRuntimeException {
	public SimpleRuntimeException() {
		super();
	}

	public SimpleRuntimeException(String message) {
		super(message);
	}

	public SimpleRuntimeException(Throwable cause) {
		super(cause);
	}

	public SimpleRuntimeException(String message, Throwable cause) {
		super(ConstantPool.ERROR_BASE_CODE, message, cause);
	}

	public <E extends BaseRuntimeException> void rethrow(Function<SimpleRuntimeException, E> mapper) {
		throw mapper.apply(this);
	}
}
