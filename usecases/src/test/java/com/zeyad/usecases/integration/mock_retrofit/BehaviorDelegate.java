package com.zeyad.usecases.integration.mock_retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Applies {@linkplain NetworkBehavior behavior} to responses and adapts them into the appropriate
 * return type using the {@linkplain Retrofit#callAdapterFactories() call adapters} of
 * {@link Retrofit}.
 *
 * @see MockRetrofit#create(Class)
 */
public final class BehaviorDelegate<T> {
    final Retrofit retrofit;
    private final NetworkBehavior behavior;
    private final ExecutorService executor;
    private final Class<T> service;

    BehaviorDelegate(Retrofit retrofit, NetworkBehavior behavior, ExecutorService executor,
                     Class<T> service) {
        this.retrofit = retrofit;
        this.behavior = behavior;
        this.executor = executor;
        this.service = service;
    }

    public T returningResponse(Object response) {
        return returning(Calls.response(response));
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <R> T returning(Call<R> call) {
        final Call<R> behaviorCall = new BehaviorCall<>(behavior, executor, call);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                (proxy, method, args) -> {
                    Type returnType = method.getGenericReturnType();
                    Annotation[] methodAnnotations = method.getAnnotations();
                    CallAdapter<R, T> callAdapter =
                            (CallAdapter<R, T>) retrofit.callAdapter(returnType, methodAnnotations);
                    return callAdapter.adapt(behaviorCall);
                });
    }
}