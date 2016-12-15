package com.zeyad.usecases.app.presentation.repo_detail;

import android.os.Bundle;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoDetailVM extends BaseViewModel {

    private final IDataUseCase dataUseCase;

    RepoDetailVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    @Override
    public Bundle getState() {
        return null;
    }

    @Override
    public void restoreState(Bundle state) {
    }
}
