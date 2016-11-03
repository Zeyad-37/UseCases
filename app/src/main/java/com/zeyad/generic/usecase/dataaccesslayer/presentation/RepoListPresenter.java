package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BasePresenter;
import com.zeyad.genericusecase.data.requests.GetRequest;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author zeyad on 11/1/16.
 */

public class RepoListPresenter extends BasePresenter implements RepoListView {

    String ENDPOINT = "https://api.ribot.io/";

    @Inject
    RepoListPresenter() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public Observable getRepoList() {
        return getGenericUseCase().getList(new GetRequest
                .GetObjectRequestBuilder(Object.class, true)
                .presentationClass(Object.class)
                .url(ENDPOINT + "r/aww/new/.json")
                .build());
    }
}
