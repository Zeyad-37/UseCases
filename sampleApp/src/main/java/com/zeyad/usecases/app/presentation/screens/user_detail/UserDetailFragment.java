package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseFragment;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends BaseFragment implements LoadDataView<UserDetailModel> {
    /**
     * The fragment argument representing the item that this fragment represents.
     */
    public static final String ARG_USER_DETAIL_MODEL = "userDetailModel";
    UserDetailVM userDetailVM;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.textView_type)
    TextView textViewType;
    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;
    private GenericRecyclerViewAdapter repositoriesAdapter;
    private UserDetailModel userDetailModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserDetailModel userDetailModel) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_DETAIL_MODEL, Parcels.wrap(userDetailModel));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(ARG_USER_DETAIL_MODEL, Parcels.wrap(userDetailModel));
        return bundle;
    }

    @Override
    public void restoreState(Bundle outState) {
        userDetailModel = Parcels.unwrap(outState.getParcelable(ARG_USER_DETAIL_MODEL));
    }

    @Override
    public void initialize() {
        viewModel = new UserDetailVM();
        userDetailVM = ((UserDetailVM) viewModel);
        if (getArguments() != null)
            userDetailModel = Parcels.unwrap(getArguments().getParcelable(ARG_USER_DETAIL_MODEL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        return rootView;
    }

    void setupRecyclerView() {
        recyclerViewRepositories.setLayoutManager(new LinearLayoutManager(getViewContext()));
        repositoriesAdapter = new GenericRecyclerViewAdapter(getViewContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepositoryViewHolder(mLayoutInflater.inflate(viewType, parent, false));
            }
        };
        recyclerViewRepositories.setAdapter(repositoriesAdapter);
    }

    @Override
    public void loadData() {
        UserRealm userRealm = userDetailModel.getUser();
        userDetailVM.getRepositories(userRealm.getLogin())
//                .flatMap(userDetailModel -> Observable.just(userDetailModel.setUserRealm(userDetailModel.getUser())))
                .flatMap(userDetailModel -> Observable.just(userDetailVM.reduce(this.userDetailModel, userDetailModel)))
                .doOnSubscribe(() -> {
                    textViewType.setText(String.format("User: %s", userRealm.getLogin()));
                    if (userDetailModel.isTwoPane()) {
                        UserListActivity activity = (UserListActivity) getActivity();
                        if (activity != null) {
                            Toolbar appBarLayout = (Toolbar) activity.findViewById(R.id.toolbar);
                            if (appBarLayout != null)
                                appBarLayout.setTitle(userRealm.getLogin());
                            if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                                Glide.with(getViewContext())
                                        .load(userRealm.getAvatarUrl())
                                        .into(activity.imageViewAvatar);
                        }
                    } else {
                        UserDetailActivity activity = (UserDetailActivity) getActivity();
                        if (activity != null) {
                            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity
                                    .findViewById(R.id.toolbar_layout);
                            if (appBarLayout != null)
                                appBarLayout.setTitle(userRealm.getLogin());
                            if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                                Glide.with(getViewContext())
                                        .load(userRealm.getAvatarUrl())
                                        .into(activity.imageViewAvatar);
                        }
                    }
                })
                .subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderModel(UserDetailModel userDetailModel) {
        this.userDetailModel = userDetailModel;
        List<RepoRealm> repoModels = userDetailModel.getRepos();
        if (Utils.isNotEmpty(repoModels))
            for (int i = 0, repoModelSize = repoModels.size(); i < repoModelSize; i++)
                repositoriesAdapter.appendItem(new ItemInfo<>(repoModels.get(i), R.layout.repo_item_layout));
    }

    @Override
    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> loaderLayout.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoading() {
        Activity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> loaderLayout.setVisibility(View.GONE));
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, loaderLayout, message, R.string.retry, view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, loaderLayout, Snackbar.LENGTH_LONG);
    }

    @Override
    public Context getViewContext() {
        return getContext();
    }

    @Override
    public UserDetailModel getModel() {
        return null;
    }
}
