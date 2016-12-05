package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import com.zeyad.generic.usecase.dataaccesslayer.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import rx.subscriptions.CompositeSubscription;

/**
 * @author by zeyad on 31/05/16.
 */
public abstract class BasePresenter {
    private final IDataUseCase mGenericUseCase;

    private CompositeSubscription mCompositeSubscription;

    public BasePresenter() {
        mGenericUseCase = DataUseCaseFactory.getInstance();
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    public abstract void resume();

    public abstract void pause();

    public void destroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public IDataUseCase getGenericUseCase() {
        return mGenericUseCase;
    }

    public CompositeSubscription getCompositeSubscription() {
        return mCompositeSubscription;
    }
}