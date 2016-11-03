package com.zeyad.generic.usecase.dataaccesslayer.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvp.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.di.components.UserComponent;

import butterknife.BindView;

/**
 * An activity representing a single Repo detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RepoListActivity}.
 */
public class RepoDetailActivity extends BaseActivity {

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initialize() {
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_repo_detail);
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own detail action",
                Snackbar.LENGTH_LONG).setAction("Action", null).show());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (mSavedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(RepoDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(RepoDetailFragment.ARG_ITEM_ID));
            RepoDetailFragment fragment = new RepoDetailFragment();
            fragment.setArguments(arguments);
            addFragment(R.id.repo_detail_container, fragment, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RepoListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
