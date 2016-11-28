package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseSubscriber;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

import static com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RepoListActivity extends BaseActivity {


    RepoListVM mRepoListVM;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.repo_list)
    RecyclerView mRepoRecycler;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    GenericRecyclerViewAdapter mReposAdapter;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
    }

    @Override
    public void setupUI(Bundle savedInstanceState) {
        setContentView(R.layout.activity_repo_list);
        setSupportActionBar(mToolbar);
        ButterKnife.bind(this);
        mToolbar.setTitle(getTitle());
        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        setupRecyclerView();
        if (findViewById(R.id.repo_detail_container) != null)
            twoPane = true;
    }

    @Override
    public Subscription loadData() {
        return mRepoListVM.getRepoList()
                .doOnSubscribe(this::showLoading)
                .subscribe(new BaseSubscriber<RepoListActivity, List>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(List list) {
                        // render data
                    }
                });
    }

    private void setupRecyclerView() {
        mReposAdapter = new GenericRecyclerViewAdapter(getApplicationContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }
        };
        mReposAdapter.setAreItemsClickable(true);
        mReposAdapter.setOnItemClickListener((position, userViewModel, holder) -> {

        });
        mRepoRecycler.setAdapter(mReposAdapter);
    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> loaderLayout.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> loaderLayout.setVisibility(View.GONE));
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, mRepoRecycler, message, "RETRY", view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, mRepoRecycler, Snackbar.LENGTH_LONG);
    }
}
