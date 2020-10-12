package com.newerty.derivedStats;

import java.util.concurrent.Callable;

import static com.google.common.truth.Truth.assertThat;

public final class TestHelpers {

    private TestHelpers() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static Exception expectException(Callable<?> fn) {
        Exception exception = null;
        try {
            fn.call();
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
        return exception;
    }

    public static void checkError(String expectedClass, String expectedMessage, Throwable error, boolean unwrap) throws ClassNotFoundException {
        assertThat(error).isNotNull();
        Throwable cause = unwrap ? getRootCause(error) : error;
        assertThat(cause).isInstanceOf(Class.forName(expectedClass));
        assertThat(cause).hasMessageThat().contains(expectedMessage);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

}
