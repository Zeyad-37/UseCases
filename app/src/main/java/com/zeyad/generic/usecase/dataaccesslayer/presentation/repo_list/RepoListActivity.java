package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.generic.usecase.dataaccesslayer.components.adapter.ItemInfo;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseActivity;
import com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseSubscriber;
import com.zeyad.generic.usecase.dataaccesslayer.components.snackbar.SnackBarFactory;
import com.zeyad.generic.usecase.dataaccesslayer.models.ui.RepoModel;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_detail.RepoDetailActivity;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.view_holders.EmptyViewHolder;
import com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.view_holders.RepoViewHolder;
import com.zeyad.generic.usecase.dataaccesslayer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

import static com.zeyad.generic.usecase.dataaccesslayer.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * An activity representing a list of Repos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RepoListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.repo_list)
    RecyclerView mRepoRecycler;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    GenericRecyclerViewAdapter mReposAdapter;
    private String currentFragTag;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;

    @Override
    public void initialize() {
        viewModel = new RepoListVM();
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_repo_list);
        setSupportActionBar(mToolbar);
        ButterKnife.bind(this);
        mToolbar.setTitle(getTitle());
        mFab.setOnClickListener(view -> showErrorWithRetry("ops"));
        setupRecyclerView();
        if (findViewById(R.id.repo_detail_container) != null)
            twoPane = true;
    }

    @Override
    public Subscription loadData() {
        return ((RepoListVM) viewModel).getRepoList("Zeyad-37")
                .doOnSubscribe(this::showLoading)
                .subscribe(new BaseSubscriber<RepoListActivity, List<RepoModel>>(this, ERROR_WITH_RETRY) {
                    @Override
                    public void onNext(List<RepoModel> repoModels) {
                        List<ItemInfo> itemInfos = new ArrayList<>(repoModels.size());
                        for (int i = 0, repoModelsSize = repoModels.size(); i < repoModelsSize; i++)
                            itemInfos.add(new ItemInfo<>(repoModels.get(i), R.layout.item_repo));
                        mReposAdapter.animateTo(itemInfos);
                    }
                });
    }

    private void setupRecyclerView() {
        mReposAdapter = new GenericRecyclerViewAdapter(getApplicationContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case R.layout.empty_view:
                        return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view, parent, false));
                    case R.layout.sticky_header:
                        return new RepoViewHolder(mLayoutInflater.inflate(R.layout.sticky_header, parent, false));
                    case R.layout.item_repo:
                        return new RepoViewHolder(mLayoutInflater.inflate(R.layout.item_repo, parent, false));
                    default:
                        return null;
                }
            }
        };
        mReposAdapter.setAreItemsClickable(true);
        mReposAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (twoPane) {
//                if (itemInfo.getData() instanceof OrderViewModel) {
                if (Utils.isNotEmpty(currentFragTag)) {
                    removeFragment(currentFragTag);
                }
//                    OrderViewModel orderViewModel = (OrderViewModel) itemInfo.getData();
//                    RepoDetailFragment orderDetailFragment = RepoDetailFragment
//                            .newInstance(orderViewModel, false);
//                    orderDetailFragment.setOrderDetailListener(OrderListActivity.this);
//                    orderId = orderViewModel.getId();
//                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + orderId;
//                    addFragment(R.id.repo_detail_container, orderDetailFragment, null, currentFragTag);
//                }
            } else {
//                navigator.navigateTo(getApplicationContext(), RepoDetailActivity.getCallingIntent(getApplicationContext(),
//                                itemInfo.getData(), false));
            }
        });
        mRepoRecycler.setAdapter(mReposAdapter);
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
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, mRepoRecycler, message, "RETRY", view -> onResume());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, mRepoRecycler, Snackbar.LENGTH_LONG);
    }
}
