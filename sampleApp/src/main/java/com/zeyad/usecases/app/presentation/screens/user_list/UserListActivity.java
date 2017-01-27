package com.zeyad.usecases.app.presentation.screens.user_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailModel;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

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
    private static final String CURRENT_PAGE = "currentPage", USER_LIST_MODEL = "userListModel";
    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;
    UserListView userListVM;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    GenericRecyclerViewAdapter usersAdapter;
    private String currentFragTag;
    private boolean twoPane;
    private UserListModel userListModel;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle(3);
        bundle.putInt(CURRENT_PAGE, userListVM.getCurrentPage());
        bundle.putParcelable(USER_LIST_MODEL, Parcels.wrap(userListModel));
        return bundle;
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            userListVM.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 0));
            userListModel = Parcels.unwrap(savedInstanceState.getParcelable(USER_LIST_MODEL));
            userRecycler.scrollToPosition(UserListModel.getyScroll());
        }
    }

    @Override
    public void initialize() {
        viewModel = new UserListVM();
        userListVM = ((UserListVM) viewModel);
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        if (findViewById(R.id.user_detail_container) != null)
            twoPane = true;
    }

    @Override
    public void loadData() {
        userListVM.getUserListFromDB()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<UserListActivity, UserListModel>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(UserListModel userListModel) {
                        super.onNext(userListModel);
                        List<UserRealm> users = userListModel.getUsers();
                        if (Utils.isNotEmpty(users)) {
                            List<ItemInfo> itemInfos = new ArrayList<>(users.size());
                            UserRealm userRealm;
                            for (int i = 0, repoModelsSize = users.size(); i < repoModelsSize; i++) {
                                userRealm = users.get(i);
                                itemInfos.add(new ItemInfo<>(userRealm, R.layout.user_item_layout)
                                        .setId(userRealm.getId()));
                            }
                            usersAdapter.setDataList(itemInfos);
                            userRecycler.scrollToPosition(UserListModel.getyScroll());
                        }
                        UserListActivity.this.userListModel = userListModel;
                    }
                });
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
                    if (Utils.isNotEmpty(currentFragTag))
                        removeFragment(currentFragTag);
                    UserRealm userModel = (UserRealm) itemInfo.getData();
                    UserDetailFragment orderDetailFragment = UserDetailFragment
                            .newInstance(new UserDetailModel().setUserRealm((UserRealm) itemInfo.getData()));
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, null, currentFragTag);
                }
            } else {
                navigator.navigateTo(getViewContext(), UserDetailActivity.getCallingIntent(getViewContext(),
                        new UserDetailModel().setUserRealm((UserRealm) itemInfo.getData())));
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
                    userListVM.incrementPage(usersAdapter.getItem(usersAdapter.getItemCount() - 1).getId());
                    UserListModel.setyScroll(firstVisibleItemPosition);
                }
            }
        });
    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> {
            loaderLayout.setVisibility(View.VISIBLE);
            loaderLayout.bringToFront();
        });
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
