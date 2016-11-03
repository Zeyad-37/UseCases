package com.zeyad.generic.usecase.dataaccesslayer.di.components;

import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ActivityModule;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.UserModule;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.RepoDetailActivity;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.RepoListActivity;

import dagger.Component;

/**
 * A scope {@link com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(RepoListActivity repoListActivity);

    void inject(RepoDetailActivity repoDetailActivity);
}