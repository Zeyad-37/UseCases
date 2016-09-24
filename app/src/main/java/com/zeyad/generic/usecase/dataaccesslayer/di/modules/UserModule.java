package com.zeyad.generic.usecase.dataaccesslayer.di.modules;

import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.genericusecase.domain.interactors.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.IGenericUseCase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides user related collaborators.
 */
@Module
public class UserModule {

    private int userId = -1;

    public UserModule() {
    }

    public UserModule(int userId) {
        this.userId = userId;
    }

    @Provides
    @PerActivity
    @Named("GenericUseCase")
    IGenericUseCase provideGenericUseCase() {
        return GenericUseCaseFactory.getInstance();
    }
}