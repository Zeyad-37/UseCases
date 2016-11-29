package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.view_holders;

import android.view.View;
import android.widget.TextView;

import com.zeyad.generic.usecase.dataaccesslayer.R;
import com.zeyad.generic.usecase.dataaccesslayer.components.adapter.GenericRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 11/29/16.
 */

public class RepoViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.tv_name)
    TextView tvName;

    public RepoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
        tvName.setText(String.valueOf(data));
    }
}
