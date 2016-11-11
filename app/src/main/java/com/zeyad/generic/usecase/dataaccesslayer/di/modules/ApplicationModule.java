package com.zeyad.generic.usecase.dataaccesslayer.di.modules;

import com.zeyad.generic.usecase.dataaccesslayer.GenericApplication;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.RxEventBusFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.NavigatorFactory;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final GenericApplication application;

    public ApplicationModule(GenericApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    IRxEventBus provideRxEventBus() {
        return RxEventBusFactory.getInstance();
    }

    @Provides
    @Singleton
    INavigator providesNavigator() {
        return NavigatorFactory.getNavigator();
    }

    @Provides
    @Singleton
    IGenericUseCase providesGenericUseCase() {
        return GenericUseCaseFactory.getInstance();
    }
}