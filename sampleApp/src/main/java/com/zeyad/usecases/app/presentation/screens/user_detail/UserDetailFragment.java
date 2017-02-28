package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.components.adapter.ItemInfo;
import com.zeyad.usecases.app.components.mvvm.BaseFragment;
import com.zeyad.usecases.app.components.mvvm.BaseSubscriber;
import com.zeyad.usecases.app.components.mvvm.LoadDataView;
import com.zeyad.usecases.app.components.snackbar.SnackBarFactory;
import com.zeyad.usecases.app.presentation.screens.user_list.UserListActivity;
import com.zeyad.usecases.app.presentation.screens.user_list.UserRealm;
import com.zeyad.usecases.app.utils.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zeyad.usecases.app.components.mvvm.BaseSubscriber.ERROR_WITH_RETRY;

/**
 * A fragment representing a single RepoRealm detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends BaseFragment implements LoadDataView<UserDetailState> {
    /**
     * The fragment argument representing the item that this fragment represents.
     */
    public static final String ARG_USER_DETAIL_MODEL = "userDetailState";
    UserDetailVM userDetailVM;
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;
    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;
    private GenericRecyclerViewAdapter repositoriesAdapter;
    private UserDetailState userDetailState;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserDetailState userDetailState) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_DETAIL_MODEL, Parcels.wrap(userDetailState));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(ARG_USER_DETAIL_MODEL, Parcels.wrap(userDetailState));
        return bundle;
    }

    @Override
    public void restoreState(Bundle outState) {
        userDetailState = Parcels.unwrap(outState.getParcelable(ARG_USER_DETAIL_MODEL));
    }

    @Override
    public void initialize() {
        userDetailVM = new UserDetailVM();
        if (getArguments() != null)
            userDetailState = Parcels.unwrap(getArguments().getParcelable(ARG_USER_DETAIL_MODEL));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        postponeEnterTransition();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
//        }
//        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        return rootView;
    }

    void setupRecyclerView() {
        recyclerViewRepositories.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesAdapter = new GenericRecyclerViewAdapter((LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepositoryViewHolder(mLayoutInflater.inflate(viewType, parent, false));
            }
        };
        recyclerViewRepositories.setAdapter(repositoriesAdapter);
    }

    @Override
    public void loadData() {
        userDetailVM.getState().compose(bindToLifecycle())
                .subscribe(new BaseSubscriber<>(this, ERROR_WITH_RETRY));
        userDetailVM.getRepositories(userDetailState);
    }

    @Override
    public void renderState(UserDetailState userDetailModel) {
        this.userDetailState = userDetailModel;
        UserRealm userRealm = userDetailModel.getUser();
        List<RepoRealm> repoModels = userDetailModel.getRepos();
        if (Utils.isNotEmpty(repoModels))
            for (int i = 0, repoModelSize = repoModels.size(); i < repoModelSize; i++)
                repositoriesAdapter.appendItem(new ItemInfo<>(repoModels.get(i), R.layout.repo_item_layout));
        if (userRealm != null) {
            RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                        activity.supportStartPostponedEnterTransition();
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                        activity.supportStartPostponedEnterTransition();
                    return false;
                }
            };
            if (userDetailState.isTwoPane()) {
                UserListActivity activity = (UserListActivity) getActivity();
                if (activity != null) {
                    Toolbar appBarLayout = (Toolbar) activity.findViewById(R.id.toolbar);
                    if (appBarLayout != null)
                        appBarLayout.setTitle(userRealm.getLogin());
                    if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                        Glide.with(getContext())
                                .load(userRealm.getAvatarUrl())
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                    else
                        Glide.with(getContext())
                                .load(((int) (Math.random() * 10)) % 2 == 0 ? "https://github.com/identicons/jasonlong.png" :
                                        "https://help.github.com/assets/images/help/profile/identicon.png")
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                }
            } else {
                UserDetailActivity activity = (UserDetailActivity) getActivity();
                if (activity != null) {
                    CollapsingToolbarLayout appBarLayout = activity.collapsingToolbarLayout;
                    if (appBarLayout != null)
                        appBarLayout.setTitle(userRealm.getLogin());
                    if (Utils.isNotEmpty(userRealm.getAvatarUrl()))
                        Glide.with(getContext())
                                .load(userRealm.getAvatarUrl())
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                    else
                        Glide.with(getContext())
                                .load(((int) (Math.random() * 10)) % 2 == 0 ? "https://github.com/identicons/jasonlong.png" :
                                        "https://help.github.com/assets/images/help/profile/identicon.png")
                                .dontAnimate()
                                .listener(requestListener)
                                .into(activity.imageViewAvatar);
                }
            }
        }
//        applyPalette();
    }

    @Override
    public void toggleLoading(boolean toggle) {
        Activity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> {
                loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
                loaderLayout.bringToFront();
            });
    }

    @Override
    public void showErrorWithRetry(String message) {
        showSnackBarWithAction(SnackBarFactory.TYPE_ERROR, loaderLayout, message, R.string.retry, view -> loadData());
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, loaderLayout, Snackbar.LENGTH_LONG);
    }

    private void applyPalette() {
        if (Utils.hasM()) {
            UserDetailActivity activity = (UserDetailActivity) getActivity();
            BitmapDrawable drawable = (BitmapDrawable) activity.imageViewAvatar.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Palette.from(bitmap).generate(palette -> activity.findViewById(R.id.coordinator_detail)
                    .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (v.getHeight() == scrollX) {
                            activity.toolbar
                                    .setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT));
                            activity.toolbar.
                                    setBackground(new ColorDrawable(palette
                                            .getLightVibrantColor(Color.TRANSPARENT)));
                        } else if (scrollY == 0) {
                            activity.toolbar.setTitleTextColor(0);
                            activity.toolbar.setBackground(null);
                        }
                    }));
        }
    }
}
