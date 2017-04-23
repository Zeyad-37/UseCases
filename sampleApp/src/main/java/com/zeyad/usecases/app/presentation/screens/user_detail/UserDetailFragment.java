package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseFragment;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.UIModel;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.presentation.screens.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.GetUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.UsersNextPageEvent;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseActivity.UI_MODEL;
import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends BaseFragment<UserDetailState> {
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;
    private UserDetailVM userDetailVM;
    private GenericRecyclerViewAdapter repositoriesAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserDetailState userDetailState) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UI_MODEL, Parcels.wrap(userDetailState));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public void initialize() {
        if (getArguments() != null)
            viewState = Parcels.unwrap(getArguments().getParcelable(UI_MODEL));
        userDetailVM = new UserDetailVM(DataUseCaseFactory.getInstance());
        events = Observable.just(new GetReposEvent(viewState.getUser().getLogin()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        postponeEnterTransition();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
//        }
//        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        recyclerViewRepositories.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesAdapter = new GenericRecyclerViewAdapter((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepositoryViewHolder(mLayoutInflater.inflate(viewType, parent, false));
            }
        };
        recyclerViewRepositories.setAdapter(repositoriesAdapter);
    }

    @Override
    public void loadData() {
        events.compose(mergeEvents(DeleteUsersEvent.class, GetUsersEvent.class, SearchUsersEvent.class,
                UsersNextPageEvent.class))
                .compose(userDetailVM.uiModels(event -> new GetReposAction(((GetReposEvent) event).getLogin()),
                        action -> userDetailVM.getRepositories(((GetReposAction) action).getLogin()),
                        (currentUIModel, newUIModel) -> {
                            UserDetailState bundle = (UserDetailState) currentUIModel.getBundle();
                            if (newUIModel.isLoading())
                                currentUIModel = UIModel.loadingState(UserDetailState.builder()
                                        .setRepos(bundle.getRepos())
                                        .setUser(bundle.getUser())
                                        .setIsTwoPane(bundle.isTwoPane())
                                        .build());
                            else if (newUIModel.isSuccessful()) {
                                currentUIModel = UIModel.successState(UserDetailState.builder()
                                        .setRepos((List<RepoRealm>) newUIModel.getBundle())
                                        .setUser(bundle.getUser())
                                        .setIsTwoPane(bundle.isTwoPane())
                                        .build());
                            } else currentUIModel = UIModel.errorState(newUIModel.getError());
                            return currentUIModel;
                        }, UIModel.idleState(viewState)))
                .compose(bindToLifecycle()).subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderState(UserDetailState userDetailState) {
        viewState = userDetailState;
        UserRealm userRealm = viewState.getUser();
        List<RepoRealm> repoModels = viewState.getRepos();
        if (Utils.isNotEmpty(repoModels))
            for (int i = 0, repoModelSize = repoModels.size(); i < repoModelSize; i++)
                repositoriesAdapter.appendItem(new ItemInfo<>(repoModels.get(i), R.layout.repo_item_layout));
        if (userRealm != null) {
            RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                           boolean isFirstResource) {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                        activity.supportStartPostponedEnterTransition();
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                               boolean isFromMemoryCache, boolean isFirstResource) {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                        activity.supportStartPostponedEnterTransition();
                    return false;
                }
            };
            if (userDetailState.isTwoPane()) {
                UserListActivity activity = (UserListActivity) getActivity();
                if (activity != null) {
                    Toolbar appBarLayout = (Toolbar) activity.findViewById(R.id.toolbar);
                    if (appBarLayout != null)
                        appBarLayout.setTitle(userRealm.getLogin());
                    if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                        Glide.with(getContext())
                                .load(userRealm.getAvatarUrl())
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                }
            } else {
                UserDetailActivity activity = (UserDetailActivity) getActivity();
                if (activity != null) {
                    CollapsingToolbarLayout appBarLayout = activity.collapsingToolbarLayout;
                    if (appBarLayout != null)
                        appBarLayout.setTitle(userRealm.getLogin());
                    if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                        Glide.with(getContext())
                                .load(userRealm.getAvatarUrl())
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                }
            }
        }
//        applyPalette();
    }

    @Override
    public void toggleViews(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, loaderLayout, message, R.string.retry, view -> loadData());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, loaderLayout, Snackbar.LENGTH_LONG);
    }

    private void applyPalette() {
        if (Utils.hasM()) {
            UserDetailActivity activity = (UserDetailActivity) getActivity();
            BitmapDrawable drawable = (BitmapDrawable) activity.imageViewAvatar.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Palette.from(bitmap).generate(palette -> activity.findViewById(R.id.coordinator_detail)
                    .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (v.getHeight() == scrollX) {
                            activity.toolbar
                                    .setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT));
                            activity.toolbar.
                                    setBackground(new ColorDrawable(palette
                                            .getLightVibrantColor(Color.TRANSPARENT)));
                        } else if (scrollY == 0) {
                            activity.toolbar.setTitleTextColor(0);
                            activity.toolbar.setBackground(null);
                        }
                    }));
        }
    }
}
