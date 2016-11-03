package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.UserComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RepoListActivity extends BaseActivity {
    @Inject
    RepoListPresenter mRepoListPresenter;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.repo_list)
    RecyclerView mRepoRecycler;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    GenericRecyclerViewAdapter mReposAdapter;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initialize() {
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_repo_list);
        setSupportActionBar(mToolbar);
        ButterKnife.bind(this);
        mToolbar.setTitle(getTitle());
        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        setupRecyclerView();
        if (findViewById(R.id.repo_detail_container) != null)
            mTwoPane = true;
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
    protected void onResume() {
        super.onResume();
        loadRepos();
    }

    private void loadRepos() {
        mRepoListPresenter.getRepoList().subscribe(new Subscriber<List>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List list) {

            }
        });
    }
}
