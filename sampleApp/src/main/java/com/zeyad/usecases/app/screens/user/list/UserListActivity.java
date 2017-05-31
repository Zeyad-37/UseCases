package com.zeyad.usecases.app.screens.user.list;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
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

import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.redux.BaseActivity;
import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.UISubscriber;
import com.zeyad.usecases.app.screens.user.detail.UserDetailActivity;
import com.zeyad.usecases.app.screens.user.detail.UserDetailFragment;
import com.zeyad.usecases.app.screens.user.detail.UserDetailState;
import com.zeyad.usecases.app.screens.user.list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.screens.user.list.events.SearchUsersEvent;
import com.zeyad.usecases.app.screens.user.list.viewHolders.EmptyViewHolder;
import com.zeyad.usecases.app.screens.user.list.viewHolders.UserViewHolder;
import com.zeyad.usecases.app.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * An activity representing a list of Repos. This activity has different presentations for handset
 * and tablet-size devices. On handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing item details. On tablets, the activity presents
 * the list of items and item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity<UserListState, UserListVM> implements ActionMode.Callback {
    public static final int PAGE_SIZE = 6;

    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @BindView(R.id.user_list)
    RecyclerView userRecycler;

    private GenericRecyclerViewAdapter usersAdapter;
    private ActionMode actionMode;
    private String currentFragTag;
    private boolean twoPane;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public void initialize() {
        errorMessageFactory = Throwable::getLocalizedMessage;
        viewModel = ViewModelProviders.of(this).get(UserListVM.class);
        viewModel.init((newResult, currentStateBundle) -> {
            List resultList = (List) newResult.getBundle();
            List<User> users = currentStateBundle == null ? new ArrayList<>() :
                    currentStateBundle.getUsers();
            if (resultList.size() > 0 && resultList.get(0) instanceof User) {
                users.addAll(resultList);
            } else {
                final Iterator<User> each = users.iterator();
                while (each.hasNext()) {
                    if (resultList.contains((long) each.next().getId())) {
                        each.remove();
                    }
                }
            }
            users = new ArrayList<>(new HashSet<>(users));
            int lastId = users.get(users.size() - 1).getId();
            Collections.sort(users, (user1, user2) ->
                    String.valueOf(user1.getId()).compareTo(String.valueOf(user2.getId())));
            return UserListState.builder().setUsers(users).lastId(lastId).build();
        }, null, DataServiceFactory.getInstance());
        events = Single.<BaseEvent>just(new GetPaginatedUsersEvent(0))
                .doOnSuccess(event -> Log.d("GetUsersEvent", "fired!"))
                .toObservable();
        rxEventBus.toFlowable()
                .compose(bindToLifecycle())
                .subscribe(stream -> events.mergeWith((Observable<DeleteUsersEvent>) stream)
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .compose(uiModelsTransformer)
                        .compose(bindToLifecycle())
                        .subscribe(new UISubscriber<>(this, errorMessageFactory)));
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        twoPane = findViewById(R.id.user_detail_container) != null;
    }

    @Override
    public void renderState(UserListState state) {
        viewState = state;
        List<User> users = viewState.getUsers();
        if (Utils.isNotEmpty(users)) {
            List<ItemInfo> itemInfoList = new ArrayList<>(users.size());
            User user;
            int usersListSize = users.size();
            for (int i = 0; i < usersListSize; i++) {
                user = users.get(i);
                itemInfoList.add(
                        new ItemInfo<>(user, R.layout.user_item_layout).setId(user.getId()));
            }
            usersAdapter.setDataList(itemInfoList);
        }
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case R.layout.empty_view:
                        return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view,
                                parent, false));
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
            } else if (itemInfo.getData() instanceof User) {
                User userModel = (User) itemInfo.getData();
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
                    if (Utils.isNotEmpty(currentFragTag)) {
                        removeFragment(currentFragTag);
                    }
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailState);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, currentFragTag, pairs);
                } else {
                    if (Utils.hasLollipop()) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                                pair, secondPair);
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this,
                                userDetailState), options);
                    } else {
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this, userDetailState));
                    }
                }
            }
        });
        usersAdapter.setOnItemLongClickListener(
                (position, itemInfo, holder) -> {
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
        events = events.mergeWith(Observable.defer(() -> RxRecyclerView.scrollStateChanges(userRecycler)
                .map(integer -> {
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    return (layoutManager.getChildCount() + firstVisibleItemPosition) >= totalItemCount &&
                            firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE ?
                            new GetPaginatedUsersEvent(viewState.getLastId()) :
                            new GetPaginatedUsersEvent(-1);
                })
                .filter(usersNextPageEvent -> usersNextPageEvent.getLastId() != -1)
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .debounce(300, TimeUnit.MILLISECONDS)
                .doOnNext(searchUsersEvent -> Log.d("NextPageEvent", "fired!"))));
    }

    @Override
    public void toggleViews(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
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
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .doOnNext(searchUsersEvent -> Log.d("SearchEvent", "eventFired")));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection viewState of an item.
     * <p>
     * <p>If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection viewState
     */

    private boolean toggleSelection(int position) {
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

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
        events = events.mergeWith(Observable.defer(() -> RxMenuItem.clicks(menu.findItem(R.id.delete_item))
                .map(click -> new DeleteUsersEvent(usersAdapter.getSelectedItemsIds()))
                .doOnEach(notification -> {
                    actionMode.finish();
                    Log.d("DeleteEvent", "fired!");
                })));
        rxEventBus.send(events);
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
        return item.getItemId() == R.id.delete_item;
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
