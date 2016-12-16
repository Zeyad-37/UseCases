package com.zeyad.usecases.app.presentation.repo_detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.mvvm.BaseFragment;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.presentation.repo_list.RepoListActivity;
import com.zeyad.usecases.app.view_models.UserModel;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link RepoListActivity}
 * in two-pane mode (on tablets) or a {@link RepoDetailActivity}
 * on handsets.
 */
public class RepoDetailFragment extends BaseFragment implements LoadDataView {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item";
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    /**
     * The content this fragment is presenting.
     */
    private UserModel userModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepoDetailFragment() {
    }

    public static RepoDetailFragment newInstance(UserModel userModel) {
        RepoDetailFragment repoDetailFragment = new RepoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ITEM, Parcels.wrap(userModel));
        repoDetailFragment.setArguments(bundle);
        return repoDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(userModel.content);
            }
        }
    }

    @Override
    public void initialize() {
        if (getArguments() != null) {
            userModel = Parcels.unwrap(getArguments().getParcelable(ARG_ITEM));
        }
    }

    @Override
    public Subscription loadData() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.repo_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
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
//        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, mRepoRecycler, message, "RETRY", view -> onResume());
    }

    @Override
    public void showError(String message) {
//        showErrorSnackBar(message, mRepoRecycler, Snackbar.LENGTH_LONG);
    }
}
