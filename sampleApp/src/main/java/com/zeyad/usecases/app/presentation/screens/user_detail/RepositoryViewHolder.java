package com.zeyad.usecases.app.presentation.screens.user_detail;

import android.view.View;
import android.widget.TextView;

import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.data.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 1/12/17.
 */
class RepositoryViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.textView_repo_title)
    TextView textView_repo_title;

    RepositoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
        if (data != null) {
            RepoRealm repoModel = (RepoRealm) data;
            if (Utils.isNotEmpty(repoModel.getName()))
                textView_repo_title.setText(repoModel.getName());
        }
    }
}
