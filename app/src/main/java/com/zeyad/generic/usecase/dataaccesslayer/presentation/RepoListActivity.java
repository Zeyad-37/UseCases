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

import java.util.ArrayList;

import butterknife.BindView;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RepoListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.repo_list)
    RecyclerView mRepoRecycler;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void setupUI() {
        setSupportActionBar(mToolbar);
//        ButterKnife.
        mToolbar.setTitle(getTitle());
        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action",
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        setupRecyclerView();
        if (findViewById(R.id.repo_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView() {
        mRepoRecycler.setAdapter(new GenericRecyclerViewAdapter(getApplicationContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }
        });
    }
}
