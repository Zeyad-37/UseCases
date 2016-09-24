package com.zeyad.generic.usecase.dataaccesslayer.di.components;

import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BaseFragment;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    IRxEventBus rxEventBus();
}
