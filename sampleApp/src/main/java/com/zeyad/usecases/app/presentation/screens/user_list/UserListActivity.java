package com.zeyad.usecases.app.presentation.screens.user_list;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.zeyad.usecases.app.presentation.models.UserRealm;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
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

    public static final int PAGE_SIZE = 6;
    UserListVM userListVM;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    GenericRecyclerViewAdapter usersAdapter;
    private String currentFragTag;
    private boolean twoPane;
    private int userId;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

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
        setupRecyclerView();
        if (findViewById(R.id.user_detail_container) != null)
            twoPane = true;
    }

    private Subscription writePeriodic() {
        return userListVM.writePeriodic()
                .compose(bindToLifecycle())
                .doOnUnsubscribe(() -> Log.d("doOnUnsubscribe", "Unsubscribing subscription from writePeriodic"))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d("OnNext", String.valueOf(aLong));
                    }
                });
    }

    private Subscription itemByItem() {
        return userListVM.updateItemByItem()
                .doOnSubscribe(this::showLoading)
                .compose(bindToLifecycle())
                .doOnUnsubscribe(() -> Log.d("doOnUnsubscribe", "Unsubscribing subscription from loadData"))
                .subscribe(new BaseSubscriber<UserListActivity, UserRealm>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(UserRealm userModel) {
                        hideLoading();
                        usersAdapter.appendItem(new ItemInfo<>(userModel, R.layout.user_item_layout));
                    }
                });
    }

    private Subscription getList() {
        return userListVM.getUserList()
                .doOnSubscribe(this::showLoading)
                .compose(bindToLifecycle())
                .doOnUnsubscribe(() -> Log.d("doOnUnsubscribe", "Unsubscribing subscription from loadData"))
                .subscribe(new BaseSubscriber<UserListActivity, List<UserRealm>>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(List<UserRealm> userModels) {
                        hideLoading();
                        List<ItemInfo> infoList = new ArrayList<>(userModels.size());
                        for (int i = 0, repoModelsSize = userModels.size(); i < repoModelsSize; i++)
                            infoList.add(new ItemInfo<>(userModels.get(i), R.layout.user_item_layout));
                        usersAdapter.setDataList(infoList);
                    }
                });
    }

    @Override
    public Subscription loadData() {
        return getList();
//        return itemByItem();
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(getViewContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case R.layout.empty_view:
                        return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view, parent,
                                false));
                    case R.layout.user_item_layout:
                        return new UserViewHolder(mLayoutInflater.inflate(R.layout.user_item_layout,
                                parent, false));
                    default:
                        return null;
                }
            }
        };
        usersAdapter.setAreItemsClickable(true);
        usersAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (twoPane) {
                if (itemInfo.getData() instanceof UserRealm) {
                    if (Utils.isNotEmpty(currentFragTag)) {
                        removeFragment(currentFragTag);
                    }
                    UserRealm userModel = (UserRealm) itemInfo.getData();
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userModel);
                    userId = userModel.getId();
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userId;
                    addFragment(R.id.user_detail_container, orderDetailFragment, null, currentFragTag);
                }
            } else {
                navigator.navigateTo(getViewContext(), UserDetailActivity
                        .getCallingIntent(getViewContext(), (UserRealm) itemInfo.getData()));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getViewContext());
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
                    loadData();
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
