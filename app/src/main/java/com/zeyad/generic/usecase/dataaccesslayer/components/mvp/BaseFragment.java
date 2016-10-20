package com.zeyad.generic.usecase.dataaccesslayer.components.mvp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.zeyad.generic.usecase.dataaccesslayer.GenericApplication;
import com.zeyad.generic.usecase.dataaccesslayer.Utils;
import com.zeyad.generic.usecase.dataaccesslayer.components.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.components.eventbus.IRxEventBus;
import com.zeyad.generic.usecase.dataaccesslayer.components.navigation.INavigator;
import com.zeyad.generic.usecase.dataaccesslayer.di.HasComponent;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.ApplicationComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

    @Inject
    public INavigator navigator;
    @Inject
    public IRxEventBus rxEventBus;
    public CompositeSubscription mCompositeSubscription;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
        setRetainInstance(true);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    public abstract void initialize();

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link com.zeyad.generic.usecase.dataaccesslayer.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((GenericApplication) getContext().getApplicationContext()).getApplicationComponent();
    }

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view, String message, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, message, duration)
                .show();
    }

    protected void showSnackBarMessage(@SnackBarFactory.SnackBarType String typeSnackBar, View view, int messageId, int duration) {
        SnackBarFactory.getSnackBar(typeSnackBar, view, getString(messageId), duration)
                .show();
    }

    public void showErrorSnackBar(String message, View view) {
        showSnackBarMessage(SnackBarFactory.TYPE_ERROR, view, message, Snackbar.LENGTH_LONG);
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        super.onDestroyView();
    }
}