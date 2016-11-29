package com.zeyad.generic.usecase.dataaccesslayer.di.components;

import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ActivityModule;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.RepoDetailActivity;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.RepoListActivity;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation:
 * {@link com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity}
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(RepoListActivity repoListActivity);

    void inject(RepoDetailActivity repoDetailActivity);
}