package com.zeyad.usecases.app.screens.user.list.viewHolders;

import android.view.View;
import android.widget.TextView;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author by ZIaDo on 7/18/17.
 */

public class SectionHeaderViewHolder extends GenericRecyclerViewAdapter.ViewHolder<String> {

    @BindView(R.id.sectionHeader)
    TextView textViewTitle;

    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(String title, boolean isItemSelected, int position, boolean isEnabled) {
        if (Utils.isNotEmpty(title)) {
            textViewTitle.setText(title);
        }
    }
}
