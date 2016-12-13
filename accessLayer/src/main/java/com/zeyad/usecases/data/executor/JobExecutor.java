package com.zeyad.usecases.data.executor;

import android.support.annotation.NonNull;

import com.zeyad.usecases.domain.executors.ThreadExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Decorated {@link ThreadPoolExecutor}
 */
public class JobExecutor implements ThreadExecutor {

    private static final int INITIAL_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    @NonNull
    private final ThreadPoolExecutor threadPoolExecutor;

    public JobExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, INITIAL_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<>(), new JobThreadFactory());
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "android_";
        private int counter = 0;

        @NonNull
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, THREAD_NAME + counter++);
        }
    }
}