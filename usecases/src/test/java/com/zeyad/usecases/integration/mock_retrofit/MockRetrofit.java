package com.zeyad.usecases.integration.mock_retrofit;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;

/**
 * @author by ZIaDo on 6/21/17.
 */
public final class MockRetrofit {
    private final Retrofit retrofit;
    private final NetworkBehavior behavior;
    private final ExecutorService executor;

    MockRetrofit(Retrofit retrofit, NetworkBehavior behavior, ExecutorService executor) {
        this.retrofit = retrofit;
        this.behavior = behavior;
        this.executor = executor;
    }

    public Retrofit retrofit() {
        return retrofit;
    }

    public NetworkBehavior networkBehavior() {
        return behavior;
    }

    public Executor backgroundExecutor() {
        return executor;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> BehaviorDelegate<T> create(Class<T> service) {
        return new BehaviorDelegate<>(retrofit, behavior, executor, service);
    }

    public static final class Builder {
        private final Retrofit retrofit;
        private NetworkBehavior behavior;
        private ExecutorService executor;

        public Builder(Retrofit retrofit) {
            if (retrofit == null) throw new NullPointerException("retrofit == null");
            this.retrofit = retrofit;
        }

        public Builder networkBehavior(NetworkBehavior behavior) {
            if (behavior == null) throw new NullPointerException("behavior == null");
            this.behavior = behavior;
            return this;
        }

        public Builder backgroundExecutor(ExecutorService executor) {
            if (executor == null) throw new NullPointerException("executor == null");
            this.executor = executor;
            return this;
        }

        public MockRetrofit build() {
            if (behavior == null) behavior = NetworkBehavior.create();
            if (executor == null) executor = Executors.newCachedThreadPool();
            return new MockRetrofit(retrofit, behavior, executor);
        }
    }
}
