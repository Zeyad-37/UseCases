package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.LoadDataView;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.models.ui.UserModel;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.RepoListActivity;

import org.parceler.Parcels;

import butterknife.BindView;
import rx.Subscription;

/**
 * An activity representing a single RepoRealm detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RepoListActivity}.
 */
public class RepoDetailActivity extends BaseActivity implements LoadDataView{
    public static final String ARG_ITEM = "item";
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
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
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (isNewActivity) {
            addFragment(R.id.repo_detail_container, RepoDetailFragment.newInstance(getIntent()
                    .getParcelableExtra(RepoDetailFragment.ARG_ITEM)), null, "");
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
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, toolbar, message, "RETRY", view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, toolbar, Snackbar.LENGTH_LONG);
    }

    public static Intent getCallingIntent(Context context, UserModel userModel) {
        return new Intent(context, RepoDetailActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(ARG_ITEM, Parcels.wrap(userModel));
    }
}
