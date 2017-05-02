package com.zeyad.usecases.app.components.redux;

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

import rx.Observable;
import rx.functions.Func2;

import static com.zeyad.usecases.app.components.redux.BaseActivity.UI_MODEL;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseFragment<S, VM extends BaseViewModel<S>> extends RxFragment implements LoadDataView<S> {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public Observable<BaseEvent> events;
    public S viewState;
    public VM viewModel;
    public Func2<UIModel<S>, Result<?>, UIModel<S>> stateAccumulator;

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
            renderState(Parcels.unwrap(savedInstanceState.getParcelable(UI_MODEL)));
    }

    @Override
    public void onResume() {
        super.onResume();
        events.compose(viewModel.uiModels(stateAccumulator, UIModel.idleState(viewState)))
                .compose(bindToLifecycle())
                .subscribe(new UISubscriber<>(this));
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
        bundle.putParcelable(UI_MODEL, Parcels.wrap(viewState));
        return bundle;
    }

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

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
        if (view != null)
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
        else throw new NullPointerException("view is null");
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, String actionText, View.OnClickListener onClickListener) {
        if (view != null)
            SnackBarFactory.getSnackBarWithAction(typeSnackBar, view, message, actionText,
                    onClickListener).show();
        else throw new NullPointerException("View is null");
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} errorResult message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        if (view != null)
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration).show();
        else throw new NullPointerException("View is null");
    }
}
