package com.zeyad.generic.usecase.dataaccesslayer.di.modules;

import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.RxEventBusFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.NavigatorFactory;
import com.zeyad.genericusecase.domain.interactors.files.FileUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.files.IFileUseCase;
import com.zeyad.genericusecase.domain.interactors.generic.GenericUseCaseFactory;
import com.zeyad.genericusecase.domain.interactors.generic.IGenericUseCase;
import com.zeyad.genericusecase.domain.interactors.prefs.IPrefsUseCase;
import com.zeyad.genericusecase.domain.interactors.prefs.PrefsUseCaseFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {

    public ApplicationModule() {
    }

    @Provides
    @Singleton
    IRxEventBus provideRxEventBus() {
        return RxEventBusFactory.getInstance();
    }

    @Provides
    @Singleton
    INavigator providesNavigator() {
        return NavigatorFactory.getInstance();
    }

    @Provides
    @Singleton
    IGenericUseCase providesGenericUseCase() {
        return GenericUseCaseFactory.getInstance();
    }

    @Provides
    @Singleton
    IPrefsUseCase providesPrefsUseCase() {
        return PrefsUseCaseFactory.getInstance();
    }

    @Provides
    @Singleton
    IFileUseCase providesFileUseCase() {
        return FileUseCaseFactory.getInstance();
    }
}
