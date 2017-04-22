package com.zeyad.usecases.app.presentation.screens.user_list;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseAction;
import com.zeyad.usecases.app.components.mvvm.BaseActivity;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.UIModel;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailActivity;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailFragment;
import com.zeyad.usecases.app.presentation.screens.user_detail.UserDetailState;
import com.zeyad.usecases.app.presentation.screens.user_list.actions.DeleteUserAction;
import com.zeyad.usecases.app.presentation.screens.user_list.actions.GetUsersAction;
import com.zeyad.usecases.app.presentation.screens.user_list.actions.SearchUsersAction;
import com.zeyad.usecases.app.presentation.screens.user_list.actions.UsersNextPageAction;
import com.zeyad.usecases.app.presentation.screens.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.GetUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.app.presentation.screens.user_list.events.UsersNextPageEvent;
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

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.user_list)
    RecyclerView userRecycler;
    private UserListVM userListVM;
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
        events = Observable.just(new GetUsersEvent());
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
        events.compose(mergeEvents(DeleteUsersEvent.class, GetUsersEvent.class, SearchUsersEvent.class,
                UsersNextPageEvent.class))
                .compose(userListVM.uiModels(event -> {
                    BaseAction action = null;
                    if (event instanceof GetUsersEvent)
                        action = new GetUsersAction();
                    else if (event instanceof DeleteUsersEvent)
                        action = new DeleteUserAction(((DeleteUsersEvent) event).getSelectedItemsIds());
                    else if (event instanceof SearchUsersEvent)
                        action = new SearchUsersAction(((SearchUsersEvent) event).getQuery());
                    else if (event instanceof UsersNextPageEvent)
                        action = new UsersNextPageAction(((UsersNextPageEvent) event).getLastId());
                    return action;
                }, action -> {
                    Observable result = Observable.empty();
                    if (action instanceof GetUsersAction)
                        result = userListVM.getUsers();
                    else if (action instanceof DeleteUserAction)
                        result = userListVM.deleteCollection(((DeleteUserAction) action).getSelectedItemsIds());
                    else if (action instanceof SearchUsersAction)
                        result = userListVM.search(((SearchUsersAction) action).getQuery());
                    else if (action instanceof UsersNextPageAction)
                        result = userListVM.incrementPage(((UsersNextPageAction) action).getLastId());
                    return result;
                }, (currentUIModel, newUIModel) -> {
                    // FIXME: 4/22/17 Apply all states
                    if (newUIModel.isLoading())
                        currentUIModel = UIModel.loadingState;
                    else if (newUIModel.isSuccessful())
                        currentUIModel = UIModel.successState(UserListState.builder()
                                .setUsers((List<UserRealm>) newUIModel.getBundle())
                                .build());
                    else currentUIModel = UIModel.errorState(newUIModel.getError());
                    return currentUIModel;
                }))
                .compose(bindToLifecycle())
                .subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
    }

    @Override
    public void renderState(UserListState state) {
        uiModel = state;
        List<UserRealm> users = uiModel.getUsers();
        if (Utils.isNotEmpty(users)) {
            List<ItemInfo> itemInfoList = new ArrayList<>(users.size());
            UserRealm userRealm;
            for (int i = 0, usersListSize = users.size(); i < usersListSize; i++) {
                userRealm = users.get(i);
                itemInfoList.add(new ItemInfo<>(userRealm, R.layout.user_item_layout).setId(userRealm.getId()));
            }
//            DiffUtil.DiffResult diffResult = DiffUtil
//                    .calculateDiff(new UserListDiffCallback(usersAdapter.getDataList(), itemInfoList));
//            diffResult.dispatchUpdatesTo(usersAdapter);
            usersAdapter.setDataList(itemInfoList);
//            userRecycler.smoothScrollToPosition(state.getYScroll());
        }
    }

    @Override
    public View getViewToToggleEnabling() {
        return null;
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
        events = events.mergeWith(Observable.defer(() -> RxRecyclerView.scrollEvents(userRecycler).map(recyclerViewScrollEvent -> {
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if ((layoutManager.getChildCount() + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
//                uiModel = new UserListState.Builder(uiModel)
//                        .setUsers(uiModel.getUsers())
//                        .setYScroll(firstVisibleItemPosition)
//                        .build();
                return new UsersNextPageEvent(uiModel.getLastId());
            }
            return null;
        }).filter(usersNextPageEvent -> usersNextPageEvent != null)
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .debounce(300, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .doOnNext(searchUsersEvent -> Log.d("nextPageEvent", "eventFired"))));
    }

    @Override
    public void toggleLoading(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
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
        events = events.mergeWith(RxSearchView.queryTextChanges(mSearchView)
                .filter(charSequence -> !charSequence.toString().isEmpty())
                .map(query -> new SearchUsersEvent(query.toString()))
                .doOnNext(searchUsersEvent -> Log.d("searchEvent", "eventFired")))
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .onBackpressureLatest();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection uiModel of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection uiModel
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
        events = events.mergeWith(Observable.defer(() -> RxMenuItem.clicks(menu.findItem(R.id.delete_item))
                .map(click -> new DeleteUsersEvent(usersAdapter.getSelectedItemsIds()))
                .doOnNext(searchUsersEvent -> Log.d("deleteEvent", "eventFired"))));
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
                events = events.mergeWith(RxMenuItem.clicks(item)
                        .map(click -> new DeleteUsersEvent(usersAdapter.getSelectedItemsIds()))
                        .doOnNext(searchUsersEvent -> Log.d("deleteEvent", "eventFired")));
//                        .subscribe(o -> {
//                            usersAdapter.removeItemsById(usersAdapter.getSelectedItemsIds());
//                            mode.finish();
//                        }, throwable -> {
//                            throw new OnErrorNotImplementedException((Throwable) throwable);
//                        });
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
}
