package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.RepoListActivity;

import butterknife.BindView;
import rx.Subscription;

/**
 * An activity representing a single RepoRealm detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RepoListActivity}.
 */
public class RepoDetailActivity extends BaseActivity {

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_repo_detail);
        setSupportActionBar(mToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (isNewActivity) {
            Bundle arguments = new Bundle();
            arguments.putString(RepoDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(RepoDetailFragment.ARG_ITEM_ID));
            RepoDetailFragment fragment = RepoDetailFragment.newInstance();
            fragment.setArguments(arguments);
            addFragment(R.id.repo_detail_container, fragment, null);
        }
    }

    @Override
    public Subscription loadData() {
        return null;
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

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, mToolbar, message, "RETRY", view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, mToolbar, Snackbar.LENGTH_LONG);
    }
}
