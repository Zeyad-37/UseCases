package com.zeyad.usecases.app.components.mvvm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.zeyad.usecases.app.components.eventbus.IRxEventBus;
import com.zeyad.usecases.app.components.eventbus.RxEventBusFactory;
import com.zeyad.usecases.app.components.navigation.INavigator;
import com.zeyad.usecases.app.components.navigation.NavigatorFactory;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.utils.Utils;

import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public IBaseViewModel viewModel;
    public CompositeSubscription compositeSubscription;
    public boolean isNewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNewActivity = savedInstanceState == null;
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        compositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        initialize();
        if (!isNewActivity && viewModel != null)
            viewModel.restoreState(savedInstanceState);
        setupUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null && viewModel != null) {
            outState.putAll(viewModel.getState());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Setup the UI.
     */
    public abstract void setupUI();

    public abstract Subscription loadData();

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment, List<Pair<View, String>> sharedElements,
                               String currentFragTag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (sharedElements != null)
            for (Pair<View, String> pair : sharedElements)
                fragmentTransaction.addSharedElement(pair.first, pair.second);
        if (currentFragTag == null || currentFragTag.isEmpty())
            fragmentTransaction.addToBackStack(fragment.getTag());
        else fragmentTransaction.addToBackStack(currentFragTag);
        fragmentTransaction.add(containerViewId, fragment, fragment.getTag()).commit();
    }

    protected void removeFragment(String tag) {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(tag))
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewModel != null)
            viewModel.onViewAttached(this, isNewActivity);
        isNewActivity = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeSubscription.add(loadData());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (viewModel != null)
            viewModel.onViewDetached();
    }

    @Override
    public void onDestroy() {
        if (viewModel != null)
            viewModel.onViewDetached();
        viewModel = null;
        super.onDestroy();
    }

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} message.
     *
     * @param message An string representing a message to be shown.
     */
    public void showSnackBarMessage(View view, String message, int duration) {
        runOnUiThread(() -> {
            if (view != null)
                SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
            else throw new NullPointerException("view is null");
        });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view, String message,
                                       String actionText, View.OnClickListener onClickListener) {
        runOnUiThread(() -> {
            if (view != null)
                SnackBarFactory.getSnackBarWithAction(typeSnackBar, view, message, actionText, onClickListener).show();
            else throw new NullPointerException("view is null");
        });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view, String message,
                                       int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} error message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        runOnUiThread(() -> {
            if (view != null)
                SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration);
            else throw new NullPointerException("view is null");
        });
    }
}
