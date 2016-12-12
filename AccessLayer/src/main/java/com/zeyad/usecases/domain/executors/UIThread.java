package com.zeyad.usecases.domain.executors;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * MainThread (UI Thread) implementation based on a {@link Scheduler}
 * which will executeGetObject actions on the Android UI thread
 */
public class UIThread implements PostExecutionThread {

    public UIThread() {
    }

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}