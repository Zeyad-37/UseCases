package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import com.zeyad.genericusecase.data.utils.Utils;
import com.zeyad.genericusecase.domain.interactors.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.IGenericUseCase;

import rx.subscriptions.CompositeSubscription;

/**
 * @author by zeyad on 31/05/16.
 */
public abstract class BasePresenter {
    private final IGenericUseCase mGenericUseCase;

    private CompositeSubscription mCompositeSubscription;

    public BasePresenter() {
        mGenericUseCase = GenericUseCaseFactory.getInstance();
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    public abstract void resume();

    public abstract void pause();

    public void destroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public IGenericUseCase getGenericUseCase() {
        return mGenericUseCase;
    }

    public CompositeSubscription getCompositeSubscription() {
        return mCompositeSubscription;
    }
}