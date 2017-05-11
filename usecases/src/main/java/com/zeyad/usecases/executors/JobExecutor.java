package com.zeyad.usecases.executors;

import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;

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
    private final JobThreadFactory jobThreadFactory;

    public JobExecutor() {
        jobThreadFactory = new JobThreadFactory();
        threadPoolExecutor = new ThreadPoolExecutor(INITIAL_POOL_SIZE, INITIAL_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<>(), jobThreadFactory);
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    @Override
    public Looper getLooper() {
        return jobThreadFactory.getLooper();
    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "android_";
        private static Looper looper;
        private int counter = 0;

        @NonNull
        @Override
        public HandlerThread newThread(@NonNull Runnable runnable) {
            HandlerThread handlerThread = new HandlerThread(THREAD_NAME + counter++);
            looper = handlerThread.getLooper();
            return handlerThread;
        }

        Looper getLooper() {
            return looper;
        }
    }
}