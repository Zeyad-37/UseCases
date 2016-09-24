package com.zeyad.generic.usecase.dataaccesslayer.di.components;

import android.support.v7.app.AppCompatActivity;

import com.zeyad.generic.usecase.dataaccesslayer.di.PerActivity;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ActivityModule;

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
    //Exposed to sub-graphs.
    AppCompatActivity activity();
}