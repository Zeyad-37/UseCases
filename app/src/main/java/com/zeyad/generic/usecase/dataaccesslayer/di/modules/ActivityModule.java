package com.zeyad.generic.usecase.dataaccesslayer.di.modules;

import android.support.v7.app.AppCompatActivity;

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
    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerActivity
    AppCompatActivity activity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    INavigator providesNavigator() {
        return NavigatorFactory.getNavigator();
    }

    @Provides
    @PerActivity
    IGenericUseCase providesGenericUseCase() {
        return GenericUseCaseFactory.getInstance();
    }
}