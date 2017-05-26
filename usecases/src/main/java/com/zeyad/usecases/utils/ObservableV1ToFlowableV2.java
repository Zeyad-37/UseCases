package com.zeyad.usecases.utils;

import io.reactivex.Flowable;

final class ObservableV1ToFlowableV2<T> extends Flowable<T> {

    private final rx.Observable<T> source;

    ObservableV1ToFlowableV2(rx.Observable<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(org.reactivestreams.Subscriber<? super T> s) {
        ObservableSubscriber<T> parent = new ObservableSubscriber<>(s);
        ObservableSubscriberSubscription parentSubscription = new ObservableSubscriberSubscription(parent);
        s.onSubscribe(parentSubscription);
        source.unsafeSubscribe(parent);
    }

    private static final class ObservableSubscriber<T> extends rx.Subscriber<T> {

        final org.reactivestreams.Subscriber<? super T> actual;
        boolean done;

        ObservableSubscriber(org.reactivestreams.Subscriber<? super T> actual) {
            this.actual = actual;
            this.request(0L); // suppress starting out with Long.MAX_VALUE
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            if (t == null) {
                unsubscribe();
                onError(new NullPointerException(
                        "The upstream 1.x Observable signalled a null value which is not supported in 2.x"));
            } else {
                actual.onNext(t);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (done) {
                io.reactivex.plugins.RxJavaPlugins.onError(e);
                return;
            }
            done = true;
            actual.onError(e);
        }

        @Override
        public void onCompleted() {
            if (done) {
                return;
            }
            done = true;
            actual.onComplete();
        }

        void requestMore(long n) {
            request(n);
        }
    }

    private static final class ObservableSubscriberSubscription implements org.reactivestreams.Subscription {

        final ObservableSubscriber<?> parent;

        ObservableSubscriberSubscription(ObservableSubscriber<?> parent) {
            this.parent = parent;
        }

        @Override
        public void request(long n) {
            parent.requestMore(n);
        }

        @Override
        public void cancel() {
            parent.unsubscribe();
        }
    }

}