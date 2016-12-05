package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail;

import android.os.Bundle;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseViewModel;
import com.zeyad.genericusecase.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.data.IDataUseCase;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoDetailVM extends BaseViewModel {

    private final IDataUseCase genericUseCase;

    RepoDetailVM() {
        genericUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Bundle getState() {
        return null;
    }

    @Override
    public void restoreState(Bundle state) {
    }
}
