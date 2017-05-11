package com.zeyad.usecases.utils;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;

public final class ReplayingShare<T> implements Observable.Transformer<T, T> {
    private static final ReplayingShare<Object> INSTANCE = new ReplayingShare<>();

    private ReplayingShare() {
    }

    /**
     * The singleton instance of this transformer.
     */
    @SuppressWarnings("unchecked") // Safe because of erasure.
    public static <T> ReplayingShare<T> instance() {
        return (ReplayingShare<T>) INSTANCE;
    }

    @Override
    public Observable<T> call(Observable<T> upstream) {
        LastSeen<T> lastSeen = new LastSeen<>();
        return upstream.doOnNext(lastSeen).share().startWith(Observable.defer(lastSeen));
    }

    private static final class LastSeen<T> implements Action1<T>, Func0<Observable<T>> {
        private static final Object NONE = new Object();

        @SuppressWarnings("unchecked") // Safe because of erasure.
        private volatile T last = (T) NONE;

        LastSeen() {
        }

        @Override
        public void call(T latest) {
            last = latest;
        }

        @Override
        public Observable<T> call() {
            T value = last;
            return value != NONE ? Observable.just(value) : Observable.empty();
        }
    }
}