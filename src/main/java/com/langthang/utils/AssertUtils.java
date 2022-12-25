package com.langthang.utils;

import com.langthang.exception.HttpError;
import org.springframework.lang.Nullable;

/**
 * Assertion utility class that assists in validating arguments.
 */
public class AssertUtils {
    private AssertUtils() {
    }

    /**
     * Check if the given object is not null.
     *
     * @param object    Object to check
     * @param throwable Exception to be thrown
     * @throws HttpError if the object is {@code null}
     */
    public static <T extends HttpError> void notNull(Object object, T throwable) {
        if (object == null) {
            throw throwable;
        }
    }

    /**
     * Check if the given object is null.
     *
     * @param object    Object to check
     * @param throwable Exception to be thrown
     * @throws HttpError if the object is {@code null}
     */
    public static <T extends HttpError> void isNull(@Nullable Object object, T throwable) {
        if (object != null) {
            throw throwable;
        }
    }

    /**
     * Check if the given expression is True.
     *
     * @param expression Expression to check
     * @param throwable  Exception to be thrown
     * @throws HttpError if the object is {@code null}
     */
    public static <T extends HttpError> void isTrue(boolean expression, T throwable) {
        if (!expression) {
            throw throwable;
        }
    }
}