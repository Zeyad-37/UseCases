package com.zeyad.generic.usecase.dataaccesslayer.components.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.generic.usecase.dataaccesslayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author by zeyad on 17/05/16.
 */
public class HeadFootViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.tvHeader)
    TextView tvHeader;

    public HeadFootViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_head_foot_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position, boolean isEnabled) {
        itemView.setEnabled(isEnabled);
        if (data instanceof String)
            tvHeader.setText((String) data);
    }
}
