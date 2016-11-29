package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseFragment;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.RepoListActivity;

import butterknife.BindView;
import rx.Subscription;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link RepoListActivity}
 * in two-pane mode (on tablets) or a {@link RepoDetailActivity}
 * on handsets.
 */
public class RepoDetailFragment extends BaseFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepoDetailFragment() {
    }

    public static RepoDetailFragment newInstance() {
        return new RepoDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public void initialize() {
//        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public Subscription loadData() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.repo_detail, container, false);
        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.repo_detail)).setText(mItem.details);
//        }
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
