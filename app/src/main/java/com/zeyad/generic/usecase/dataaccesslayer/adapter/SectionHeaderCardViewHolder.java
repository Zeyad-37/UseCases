package com.zeyad.generic.usecase.dataaccesslayer.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.generic.usecase.dataaccesslayer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author zeyad on 8/3/16.
 */
public class SectionHeaderCardViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @Bind(R.id.tvCardHeader)
    TextView tvSectionHeader;

    public SectionHeaderCardViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.card_section_header_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position, boolean isEnabled) {
        itemView.setEnabled(isEnabled);
        if (data instanceof String)
            tvSectionHeader.setText((String) data);
    }
}