package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zeyad.generic.usecase.dataaccesslayer.GenericApplication;
import com.zeyad.generic.usecase.dataaccesslayer.components.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.di.HasComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.ApplicationComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.DaggerUserComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.UserComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.modules.ActivityModule;
import com.zeyad.genericusecase.data.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Activity} class for every Activity in this application.
 */
public abstract class BaseActivity extends AppCompatActivity implements HasComponent<UserComponent> {
    @Inject
    public INavigator navigator;
    @Inject
    public IRxEventBus rxEventBus;
    public CompositeSubscription mCompositeSubscription;
    private UserComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        initializeInjector();
        initialize();
        setupUI();
    }

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Setup the UI.
     */
    public abstract void setupUI();

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment, List<Pair<View, String>> sharedElements) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (sharedElements != null)
            for (Pair<View, String> pair : sharedElements)
                fragmentTransaction.addSharedElement(pair.first, pair.second);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.add(containerViewId, fragment, fragment.getTag()).commit();
    }

    protected void removeFragment(String tag) {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(tag))
                .commit();
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link com.zeyad.generic.usecase.dataaccesslayer.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((GenericApplication) getApplicationContext()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link com.zeyad.generic.usecase.dataaccesslayer.di.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) this).getComponent());
    }

    @Override
    public void onDestroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
//        RappiApplication.getRefWatcher(getApplicationContext()).watch(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, message, duration)
                .show();
    }

    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       int messageId, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, getString(messageId), duration)
                .show();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }
}