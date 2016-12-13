package com.zeyad.generic.usecase.dataaccesslayer.components.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.generic.usecase.dataaccesslayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 8/3/16.
 */
public class SectionHeaderCardViewHolder extends GenericRecyclerViewAdapter.ViewHolder {

    @Nullable
    @BindView(R.id.tvCardHeader)
    TextView tvSectionHeader;


    public SectionHeaderCardViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.card_section_header_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }

    public SectionHeaderCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean itemSelected, int position, boolean isEnabled) {
        itemView.setEnabled(isEnabled);
        if (data instanceof String) {
            if (tvSectionHeader != null) {
                tvSectionHeader.setText((String) data);
            }
        }
    }
}