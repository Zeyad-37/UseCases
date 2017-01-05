package com.zeyad.usecases.app.presentation.user_list;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.models.UserModel;
import com.zeyad.usecases.app.presentation.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity implements LoadDataView {

    public static final int PAGE_SIZE = 4;
    UserListVM userListVM;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    GenericRecyclerViewAdapter usersAdapter;
    private String currentFragTag;
    private boolean twoPane;
    private int userId;

    @Override
    public void initialize() {
        viewModel = new UserListVM();
        userListVM = ((UserListVM) viewModel);
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_list);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        toolbar.setTitle(getTitle());
        fab.setOnClickListener(view -> showErrorWithRetry("ops"));
        setupRecyclerView();
        if (findViewById(R.id.user_detail_container) != null)
            twoPane = true;
    }

    @Override
    public Subscription loadData() {
        return userListVM.getUserList()
                .doOnSubscribe(this::showLoading)
                .subscribe(new BaseSubscriber<UserListActivity, List<UserModel>>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(List<UserModel> repoModels) {
                        List<ItemInfo> infoList = new ArrayList<>(repoModels.size());
                        for (int i = 0, repoModelsSize = repoModels.size(); i < repoModelsSize; i++)
                            infoList.add(new ItemInfo<>(repoModels.get(i), R.layout.user_item_layout));
                        usersAdapter.animateTo(infoList);
                    }
                });
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(getApplicationContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case R.layout.empty_view:
                        return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view, parent, false));
                    case R.layout.user_item_layout:
                        return new UserViewHolder(mLayoutInflater.inflate(R.layout.user_item_layout, parent, false));
                    default:
                        return null;
                }
            }
        };
        usersAdapter.setAreItemsClickable(true);
        usersAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (twoPane) {
                if (itemInfo.getData() instanceof UserModel) {
                    if (Utils.isNotEmpty(currentFragTag)) {
                        removeFragment(currentFragTag);
                    }
                    UserModel userModel = (UserModel) itemInfo.getData();
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userModel);
                    userId = userModel.getId();
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userId;
                    addFragment(R.id.user_detail_container, orderDetailFragment, null, currentFragTag);
                }
            } else {
                navigator.navigateTo(getApplicationContext(), UserDetailActivity.getCallingIntent(getApplicationContext(),
                        (UserModel) itemInfo.getData()));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setAdapter(usersAdapter);
        userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if ((layoutManager.getChildCount() + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    userListVM.incrementPage();
                    mCompositeSubscription.add(loadData());
                    userListVM.setYScroll(dy);
                }
            }
        });
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
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, userRecycler, message, R.string.retry,
                view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, userRecycler, Snackbar.LENGTH_LONG);
    }

    @Override
    public Context getViewContext() {
        return this;
    }
}
