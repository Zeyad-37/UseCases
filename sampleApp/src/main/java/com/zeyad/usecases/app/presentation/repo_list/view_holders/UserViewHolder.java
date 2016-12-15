package com.zeyad.usecases.app.presentation.repo_list.view_holders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.DraweeView;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 12/1/16.
 */
public class UserViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.avatar)
    DraweeView mAvatar;
    @BindView(R.id.rl_row_user)
    RelativeLayout rl_row_user;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
    }
}
