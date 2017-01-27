package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;

import org.parceler.Parcels;

import butterknife.BindView;

import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment.ARG_USER_DETAIL_MODEL;

/**
 * An activity representing a single RepoRealm detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link UserListActivity}.
 */
public class UserDetailActivity extends BaseActivity implements LoadDataView {
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.imageView_avatar)
    ImageView imageViewAvatar;

    public static Intent getCallingIntent(Context context, UserDetailModel userDetailModel) {
        return new Intent(context, UserDetailActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(ARG_USER_DETAIL_MODEL, Parcels.wrap(userDetailModel));
    }

    @Override
    public Bundle saveState() {
        return new Bundle(0);
    }

    @Override
    public void restoreState(Bundle outState) {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_detail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (isNewActivity)
            addFragment(R.id.user_detail_container, UserDetailFragment.newInstance(Parcels.unwrap(getIntent()
                    .getParcelableExtra(ARG_USER_DETAIL_MODEL))), null, "");
    }

    @Override
    public void loadData() {
    }

    @Override
    public void onBackPressed() {
        navigateUpTo(new Intent(this, UserListActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, UserListActivity.class));
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
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, toolbar, message, R.string.retry, view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, toolbar, Snackbar.LENGTH_LONG);
    }

    @Override
    public Context getViewContext() {
        return this;
    }
}
