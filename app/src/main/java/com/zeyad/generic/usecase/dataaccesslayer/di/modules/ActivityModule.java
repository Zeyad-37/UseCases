package com.zeyad.generic.usecase.dataaccesslayer.di.modules;

import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.RxEventBusFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.NavigatorFactory;
import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity state and expose it to the graph.
 */
@Module
public class ActivityModule {

    public ActivityModule() {
    }

    @Provides
    @PerActivity
    IRxEventBus provideRxEventBus() {
        return RxEventBusFactory.getInstance();
    }

    @Provides
    @PerActivity
    INavigator providesNavigator() {
        return NavigatorFactory.getInstance();
    }

    @Provides
    @PerActivity
    IGenericUseCase providesGenericUseCase() {
        return GenericUseCaseFactory.getInstance();
    }
}