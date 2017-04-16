package com.zeyad.usecases.app.presentation.screens.user_list;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.EmptyViewHolder;
import com.zeyad.usecases.app.presentation.screens.user_list.view_holders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;
import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.NO_ERROR;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity<UserListState> implements ActionMode.Callback {
    public static final int PAGE_SIZE = 6;
    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;
    UserListViewModel userListVM;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    private GenericRecyclerViewAdapter usersAdapter;
    private boolean twoPane;
    private ActionMode actionMode;
    private String currentFragTag;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public void initialize() {
        userListVM = new UserListVM(DataUseCaseFactory.getInstance());
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_list);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        if (findViewById(R.id.user_detail_container) != null)
            twoPane = true;
    }

    @Override
    public void loadData() {
        userListVM.getUsers().compose(bindToLifecycle())
                .subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderState(UserListState state) {
        viewState = state;
        List<UserRealm> users = viewState.getUsers();
        if (Utils.isNotEmpty(users)) {
            List<ItemInfo> itemInfoList = new ArrayList<>(users.size());
            UserRealm userRealm;
            for (int i = 0, repoModelsSize = users.size(); i < repoModelsSize; i++) {
                userRealm = users.get(i);
                itemInfoList.add(new ItemInfo<>(userRealm, R.layout.user_item_layout).setId(userRealm.getId()));
            }

            DiffUtil.DiffResult diffResult = DiffUtil
                    .calculateDiff(new UserListDiffCallback(usersAdapter.getDataList(), itemInfoList));
            diffResult.dispatchUpdatesTo(usersAdapter);

            usersAdapter.setDataList(itemInfoList);
            userRecycler.smoothScrollToPosition(state.getYScroll());
        }
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter((LayoutInflater) getSystemService(Context
                .LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
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
            if (actionMode != null) {
                toggleSelection(position);
            } else if (itemInfo.getData() instanceof UserRealm) {
                UserRealm userModel = (UserRealm) itemInfo.getData();
                UserDetailState userDetailState = UserDetailState.builder()
                        .setUser(userModel)
                        .setIsTwoPane(twoPane)
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
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailState);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, currentFragTag, pairs);
                } else {
                    if (Utils.hasLollipop()) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                                pair, secondPair);
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this,
                                userDetailState), options);
                    } else
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this, userDetailState));
                }
            }
        });
        usersAdapter.setOnItemLongClickListener((position, itemInfo, holder) -> {
            if (usersAdapter.isSelectionAllowed()) {
                actionMode = startSupportActionMode(UserListActivity.this);
                toggleSelection(position);
            }
            return true;
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userRecycler.setLayoutManager(layoutManager);
        userRecycler.setAdapter(usersAdapter);
        usersAdapter.setAllowSelection(true);
        userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if ((layoutManager.getChildCount() + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    viewState = new UserListState.Builder(viewState)
                            .setUsers(viewState.getUsers())
                            .setYScroll(firstVisibleItemPosition)
                            .build();
                    userListVM.incrementPage().compose(bindToLifecycle())
                            .subscribe(new BaseSubscriber<>(UserListActivity.this));
                }
            }
        });
    }

    @Override
    public void toggleLoading(boolean toggle) {
        runOnUiThread(() -> {
            loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
            loaderLayout.bringToFront();
        });
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, userRecycler, message, R.string.retry,
                view -> loadData());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, userRecycler, Snackbar.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        Observable.defer(() -> RxSearchView.queryTextChanges(mSearchView)
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .onErrorResumeNext(Observable.empty()))
                .flatMap(query -> userListVM.search(query.toString())
                        .onErrorResumeNext(throwable -> Observable.empty()))
                .compose(bindToLifecycle())
                .subscribe(new BaseSubscriber<>(this, NO_ERROR));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection viewState of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection viewState
     */
    private boolean toggleSelection(int position) {
        try {
            if (usersAdapter.isSelectionAllowed()) {
                usersAdapter.toggleSelection(position);
                int count = usersAdapter.getSelectedItemCount();
                if (count == 0) {
                    actionMode.finish();
                } else {
                    actionMode.setTitle(String.valueOf(count));
                    actionMode.invalidate();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.delete_item).setVisible(true).setEnabled(true);
        toolbar.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                RxMenuItem.clicks(item)
                        .map(click -> new DeleteUsersEvent(usersAdapter.getSelectedItemsIds()))
                        .compose(deleteUsersEvents -> deleteUsersEvents
                                .flatMap(deleteUsersEvent -> userListVM
                                        .deleteCollection(deleteUsersEvent.getSelectedItemsIds())))
                        .compose(bindToLifecycle())
                        .subscribe(o -> {
                            usersAdapter.removeItemsById(usersAdapter.getSelectedItemsIds());
                            mode.finish();
                        }, throwable -> {
                            throw new OnErrorNotImplementedException(throwable);
                        });
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        try {
            usersAdapter.clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionMode = null;
        toolbar.setVisibility(View.VISIBLE);
    }

//    private <E extends BaseEvent, S extends ViewState> void flux(android.support.v4.util.Pair<Observable, Class> result) {
//        Observable.Transformer<E, S> eventResult = events -> events.flatMap(event -> result.first);
//        Observable.Transformer<E, S> submitUI =
//                deleteUsersEventObservable -> deleteUsersEventObservable.publish(shared -> Observable
//                        .merge(shared.ofType(DeleteUsersEvent.class).compose(eventResult),
//                                shared.ofType(DeleteUsersEvent.class).compose(delete)));
//    }
}
