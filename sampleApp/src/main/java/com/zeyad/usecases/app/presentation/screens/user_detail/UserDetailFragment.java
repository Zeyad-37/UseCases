package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseFragment;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.models.RepoModel;
import com.zeyad.usecases.app.presentation.models.UserModel;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends BaseFragment implements LoadDataView {
    /**
     * The fragment argument representing the item that this fragment represents.
     */
    public static final String ARG_USER = "user";
    UserDetailVM userDetailVM;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.textView_type)
    TextView textViewType;
    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;
    private GenericRecyclerViewAdapter repositoriesAdapter;
    private UserModel userModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserModel userModel) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER, Parcels.wrap(userModel));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putParcelable(UserDetailFragment.ARG_USER, Parcels.wrap(userModel));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initialize() {
        viewModel = new UserDetailVM();
        userDetailVM = ((UserDetailVM) viewModel);
        if (getArguments() != null) {
            userModel = Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        }
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
    public Subscription loadData() {
        return userDetailVM.getRepositories(userModel.getLogin())
                .doOnSubscribe(() -> {
                    showLoading();
                    textViewType.setText(userModel.getType());
                    UserDetailActivity activity = (UserDetailActivity) getActivity();
                    if (activity != null) {
                        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                        if (appBarLayout != null) {
                            appBarLayout.setTitle(userModel.getLogin());
                        }
                        activity.imageViewAvatar.setImageURI(Uri.parse(userModel.getAvatarUrl()));
                    }
                })
                .subscribe(new BaseSubscriber<UserDetailFragment, List<RepoModel>>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(List<RepoModel> repoModels) {
                        List<ItemInfo> infoList = new ArrayList<>(repoModels.size());
                        for (int i = 0, repoModelSize = repoModels.size(); i < repoModelSize; i++) {
                            infoList.add(new ItemInfo<>(repoModels.get(i), R.layout.repo_item_layout));
                        }
                        repositoriesAdapter.animateTo(infoList);
                    }
                });
    }

    @Override
    public void showLoading() {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> loaderLayout.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoading() {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> loaderLayout.setVisibility(View.GONE));
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
}
