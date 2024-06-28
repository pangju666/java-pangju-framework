package io.github.pangju666.framework.core.utils;

import io.github.pangju666.framework.core.exception.data.*;

public class DataAccessAssert {
    protected DataAccessAssert() {
    }

    public static void query(boolean expression) {
        if (!expression) {
            throw new DataQueryFailureException();
        }
    }

    public static void query(boolean expression, final String message) {
        if (!expression) {
            throw new DataQueryFailureException(message);
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
