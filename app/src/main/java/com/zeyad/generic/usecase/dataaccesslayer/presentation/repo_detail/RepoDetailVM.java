package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail;

import android.os.Bundle;

import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseViewModel;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoDetailVM extends BaseViewModel {

    private final IGenericUseCase genericUseCase;

    RepoDetailVM() {
        genericUseCase = GenericUseCaseFactory.getInstance();
    }

    @Override
    public Bundle getState() {
        return null;
    }

    @Override
    public void restoreState(Bundle state) {
    }
}
