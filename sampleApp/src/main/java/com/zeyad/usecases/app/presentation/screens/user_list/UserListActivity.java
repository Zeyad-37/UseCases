package com.zeyad.usecases.app.presentation.screens.user_list;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;
import static com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState.INITIAL;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity implements LoadDataView<UserListState> {
    public static final int PAGE_SIZE = 6;
    private static final String USER_LIST_MODEL = "userListState";
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
    private boolean twoPane;
    private String currentFragTag;
    private UserListState userListState;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(USER_LIST_MODEL, Parcels.wrap(userListState));
        return bundle;
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            renderState(Parcels.unwrap(savedInstanceState.getParcelable(USER_LIST_MODEL)));
            if (userListVM != null) userListVM.setCurrentPage(userListState.getCurrentPage());
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
            if (itemInfo.getData() instanceof UserRealm) {
                UserRealm userModel = (UserRealm) itemInfo.getData();
                UserDetailState userDetailModel = UserDetailState.builder()
                        .setUser(userModel)
                        .setIsTwoPane(twoPane)
                        .setState(INITIAL)
                        .build();
                Pair<View, String> pair = null;
                Pair<View, String> secondPair = null;
                if (Utils.hasLollipop()) {
                    UserViewHolder userViewHolder = (UserViewHolder) holder;
                    ImageView avatar = userViewHolder.getAvatar();
                    pair = Pair.create(avatar, avatar.getTransitionName());
                    TextView textViewTitle = userViewHolder.getTextViewTitle();
                    secondPair = Pair.create(textViewTitle, textViewTitle.getTransitionName());
                }
                if (twoPane) {
                    List<Pair<View, String>> pairs = new ArrayList<>();
                    pairs.add(pair);
                    pairs.add(secondPair);
                    if (Utils.isNotEmpty(currentFragTag))
                        removeFragment(currentFragTag);
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailModel);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, pairs, currentFragTag);
                } else {
                    if (Utils.hasLollipop()) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                                pair, secondPair);
                        navigator.navigateTo(getViewContext(), UserDetailActivity.getCallingIntent(getViewContext(),
                                userDetailModel), options);
                    } else
                        navigator.navigateTo(getViewContext(), UserDetailActivity.getCallingIntent(getViewContext(),
                                userDetailModel));
                }
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
                    userListState = new UserListState.Builder()
                            .setUsers(userListState.getUsers())
                            .setState(userListState.getState())
                            .setError(userListState.getError())
                            .setIsLoading(userListState.isLoading())
                            .setCurrentPage(userListState.getCurrentPage() + 1)
                            .setyScroll(firstVisibleItemPosition)
                            .build();
                    userListVM.incrementPage(usersAdapter.getItem(usersAdapter.getItemCount() - 1).getId());
                }
            }
        });
    }

    @Override
    public void loadData() {
        userListVM.getUsers().compose(bindToLifecycle()).subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderState(UserListState userListModel) {
        this.userListState = userListModel;
        List<UserRealm> users = userListModel.getUsers();
        if (Utils.isNotEmpty(users)) {
            List<ItemInfo> itemInfos = new ArrayList<>(users.size());
            UserRealm userRealm;
            for (int i = 0, repoModelsSize = users.size(); i < repoModelsSize; i++) {
                userRealm = users.get(i);
                itemInfos.add(new ItemInfo<>(userRealm, R.layout.user_item_layout)
                        .setId(userRealm.getId()));
            }
            usersAdapter.animateTo(itemInfos);
            userRecycler.smoothScrollToPosition(userListModel.getyScroll());
        }
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

    @Override
    public UserListState getState() {
        return userListState;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.list_menu, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        RxSearchView.queryTextChanges(mSearchView)
//                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
//                .throttleLast(100, TimeUnit.MILLISECONDS)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .onBackpressureLatest()
////                .doOnNext(query -> mUserListPresenter.search(query.toString()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorResumeNext(Observable.empty())
//                .subscribe(new DefaultSubscriber<CharSequence>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(CharSequence newText) {
//                        mUserListPresenter.search(newText.toString());
//                    }
//                });
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                mUserListPresenter.search(query);
//                return true;
//            }
//
//            // TODO: 5/28/16 use animate to!
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (newText.isEmpty())
//                    mUserListPresenter.showItemsListInView(mUserListPresenter.getItemsViewModels());
//                else
//                    mUserListPresenter.search(newText);
//                Bundle bundle = new Bundle();
//                bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, newText);
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
//                return true;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private boolean toggleSelection(int position) {
//        try {
//            if (mUsersAdapter.isSelectionAllowed()) {
//                mUsersAdapter.toggleSelection(position);
//                int count = mUsersAdapter.getSelectedItemCount();
//                if (count == 0) {
//                    actionMode.finish();
//                } else {
//                    actionMode.setTitle(String.valueOf(count));
//                    actionMode.invalidate();
//                }
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return false;
    }

//    @Override
//    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//        menu.findItem(R.id.delete_item).setVisible(true).setEnabled(true);
//        mToolbar.setVisibility(View.GONE);
//        return true;
//    }

//    @Override
//    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete_item:
//                mUserListPresenter.deleteCollection(mUsersAdapter.getSelectedItemsIds());
//                mode.finish();
//                return true;
//            default:
//                return false;
//        }
//    }

//    @Override
//    public void onDestroyActionMode(ActionMode mode) {
//        try {
//            mUsersAdapter.clearSelection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        actionMode = null;
//        mToolbar.setVisibility(View.VISIBLE);
//    }
}
