package com.zeyad.usecases.app.screens.user.detail;

import android.view.View;
import android.widget.TextView;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 1/12/17.
 */
class RepositoryViewHolder extends GenericRecyclerViewAdapter.ViewHolder<Repository> {
    @BindView(R.id.textView_repo_title)
    TextView textView_repo_title;

    RepositoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Repository repository, boolean isItemSelected, int position, boolean isEnabled) {
        if (repository != null && Utils.isNotEmpty(repository.getName())) {
            textView_repo_title.setText(repository.getName());
        }
    }
}
