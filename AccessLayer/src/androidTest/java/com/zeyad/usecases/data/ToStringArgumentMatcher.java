package com.zeyad.usecases.data;

import android.support.annotation.NonNull;

import org.mockito.ArgumentMatcher;

public class ToStringArgumentMatcher<T> extends ArgumentMatcher<T> {

    private final T mExpected;

    ToStringArgumentMatcher(T expected) {
        mExpected = expected;
    }

    @NonNull
    public static <T> ArgumentMatcher<T> newInstance(T expected) {
        return new ToStringArgumentMatcher<>(expected);
    }

    @Override
    public boolean matches(@NonNull Object actual) {
        return mExpected.toString().equals(actual.toString());
    }
}
