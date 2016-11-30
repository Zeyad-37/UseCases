package com.zeyad.generic.usecase.dataaccesslayer.presentation.repo_list.view_holders;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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
        SimpleDraweeView draweeView = new SimpleDraweeView(itemView.getContext());
        draweeView.setImageURI(Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png"));
    }
}
