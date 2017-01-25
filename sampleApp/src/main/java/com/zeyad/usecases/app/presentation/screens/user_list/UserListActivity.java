package com.zeyad.usecases.app.presentation.screens.user_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.parceler.Parcels;

import java.util.ArrayList;

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
    public static final int PAGE_SIZE = 6;
    private static final String CURRENT_PAGE = "currentPage", Y_SCROLL = "yScroll", USER_LIST_MODEL = "userListModel";
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
    private int userId, yScroll;
    private UserListModel userListModel;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            userListVM.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 0));
            yScroll = savedInstanceState.getInt(Y_SCROLL, 0);
            userListModel = Parcels.unwrap(savedInstanceState.getParcelable(USER_LIST_MODEL));
//            userListActivity.userRecycler.scrollToPosition(yScroll);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(CURRENT_PAGE, userListVM.getCurrentPage());
            outState.putInt(Y_SCROLL, yScroll);
            outState.putParcelable(USER_LIST_MODEL, Parcels.wrap(userListModel));
        }
        super.onSaveInstanceState(outState);
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
                .subscribe(o -> Log.d("OnNext", String.valueOf(o)));
    }

    private Subscription itemByItem() {
        return userListVM.updateItemByItem()
                .doOnSubscribe(this::showLoading)
                .compose(bindToLifecycle())
                .subscribe();
//                .subscribe(new BaseSubscriber<UserListActivity, UserRealm>(this, ERROR_WITH_RETRY) {
//                    @Override
//                    public void onNext(UserRealm userModel) {
//                        hideLoading();
//                        usersAdapter.appendItem(new ItemInfo<>(userModel, R.layout.user_item_layout));
//                    }
//                });
    }

    private Subscription getList() {
        return userListVM.getUserList()
                .doOnSubscribe(this::showLoading)
                .compose(bindToLifecycle())
                .subscribe(new BaseSubscriber<UserListActivity, UserListModel>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(UserListModel userListModel) {
                        super.onNext(userListModel);
                        if (Utils.isNotEmpty(userListModel.getUsers()))
                            for (int i = 0, repoModelsSize = userListModel.getUsers().size(); i < repoModelsSize; i++)
                                usersAdapter.appendItem(new ItemInfo<>(userListModel.getUsers().get(i),
                                        R.layout.user_item_layout));
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
                    yScroll = dy;
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
