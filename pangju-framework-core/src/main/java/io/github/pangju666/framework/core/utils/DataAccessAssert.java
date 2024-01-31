package io.github.pangju666.framework.core.utils;

import io.github.pangju666.framework.core.exception.data.operation.DataCreateFailureException;
import io.github.pangju666.framework.core.exception.data.operation.DataRemoveFailureException;
import io.github.pangju666.framework.core.exception.data.operation.DataSaveFailureException;
import io.github.pangju666.framework.core.exception.data.operation.DataUpdateFailureException;
import io.github.pangju666.framework.core.exception.data.query.DataRecordExistException;
import io.github.pangju666.framework.core.exception.data.query.DataRecordNotExistException;

public class DataAccessAssert {
	protected DataAccessAssert() {
	}

	public static void notExist(boolean expression) {
		if (!expression) {
			throw new DataRecordExistException();
		}
	}

	public static void notExist(boolean expression, final String message) {
		if (!expression) {
			throw new DataRecordExistException(message);
		}
	}

	public static void exist(boolean expression) {
		if (!expression) {
			throw new DataRecordNotExistException();
		}
	}

	public static void exist(boolean expression, final String message) {
		if (!expression) {
			throw new DataRecordNotExistException(message);
		}
	}

	public static void create(boolean expression) {
		if (!expression) {
			throw new DataCreateFailureException();
		}
	}

	public static void create(boolean expression, final String message) {
		if (!expression) {
			throw new DataCreateFailureException(message);
		}
	}

	public static void update(boolean expression) {
		if (!expression) {
			throw new DataUpdateFailureException();
		}
	}

	public static void update(boolean expression, final String message) {
		if (!expression) {
			throw new DataUpdateFailureException(message);
		}
	}

	public static void remove(boolean expression) {
		if (!expression) {
			throw new DataRemoveFailureException();
		}
	}

	public static void remove(boolean expression, final String message) {
		if (!expression) {
			throw new DataRemoveFailureException(message);
		}
	}

	public static void save(boolean expression) {
		if (!expression) {
			throw new DataSaveFailureException();
		}
	}

	public static void save(boolean expression, final String message) {
		if (!expression) {
			throw new DataSaveFailureException(message);
		}
	}
}
