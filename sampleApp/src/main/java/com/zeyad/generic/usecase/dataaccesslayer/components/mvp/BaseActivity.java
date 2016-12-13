package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.utils.Utils;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Activity} class for every Activity in this application.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public CompositeSubscription mCompositeSubscription;
    boolean isNewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNewActivity = (savedInstanceState == null);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
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

    @Override
    protected void onStart() {
        super.onStart();
        isNewActivity = false;
    }

    @Override
    public void onDestroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
//        RappiApplication.getRefWatcher(getApplicationContext()).watch(this);
        super.onDestroy();
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

    public void showErrorSnackBar(String message, View view) {
        showSnackBarMessage(SnackBarFactory.TYPE_ERROR, view, message, Snackbar.LENGTH_LONG);
    }
}
