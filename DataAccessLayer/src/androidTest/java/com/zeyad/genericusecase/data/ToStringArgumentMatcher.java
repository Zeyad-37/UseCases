package com.zeyad.genericusecase.data;

import org.mockito.ArgumentMatcher;

public class ToStringArgumentMatcher<T> extends ArgumentMatcher<T> {

    private final T mExpected;

    ToStringArgumentMatcher(T expected) {
        mExpected = expected;
    }

    public static <T> ArgumentMatcher<T> newInstance(T expected) {
        return new ToStringArgumentMatcher<>(expected);
    }

    @Override
    public boolean matches(Object actual) {
        return mExpected.toString().equals(actual.toString());
    }
}
