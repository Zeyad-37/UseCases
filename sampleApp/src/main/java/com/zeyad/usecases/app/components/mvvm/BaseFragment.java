package com.zeyad.usecases.app.components.mvvm;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;
import com.zeyad.usecases.app.components.eventbus.IRxEventBus;
import com.zeyad.usecases.app.components.eventbus.RxEventBusFactory;
import com.zeyad.usecases.app.components.navigation.INavigator;
import com.zeyad.usecases.app.components.navigation.NavigatorFactory;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;

import org.parceler.Parcels;

import butterknife.Unbinder;

import static com.zeyad.usecases.app.components.mvvm.BaseActivity.VIEW_STATE;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseFragment<S> extends RxFragment implements LoadDataView<S> {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public S viewState;
    public Unbinder unbinder;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        initialize();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            renderState(Parcels.unwrap(savedInstanceState.getParcelable(VIEW_STATE)));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putAll(saveState());
        super.onSaveInstanceState(outState);
    }

    /**
     * To implement! Saves the viewState of the current view. Do not return null!
     *
     * @return {@link Bundle}
     */
    private Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(VIEW_STATE, Parcels.wrap(viewState));
        return bundle;
    }

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    public abstract void loadData();

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} message.
     *
     * @param message An string representing a message to be shown.
     */
    public void showSnackBarMessage(View view, String message, int duration) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
                else throw new NullPointerException("view is null");
            });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, String actionText, View.OnClickListener onClickListener) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBarWithAction(typeSnackBar, view, message, actionText,
                            onClickListener).show();
                else throw new NullPointerException("View is null");
            });
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} errorState message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (view != null)
                    SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration).show();
                else throw new NullPointerException("View is null");
            });
    }
}
