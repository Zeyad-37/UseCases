package com.zeyad.generic.usecase.dataaccesslayer.components.adapter;

import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.zeyad.generic.usecase.dataaccesslayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 8/3/16.
 */
public class SimpleCardViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.tvSimpleText)
    TextView tvSimpleText;
    @BindView(R.id.tvSimpleTextValue)
    TextView tvSimpleTextValue;
//    @BindView(R.id.vSplitter)
//    View vSplitter;

    public SimpleCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean itemSelected, int position, boolean isEnabled) {
        itemView.setEnabled(isEnabled);
//        vSplitter.setVisibility(View.VISIBLE);
        tvSimpleTextValue.setVisibility(View.VISIBLE);
        if (data instanceof String) {
            tvSimpleText.setText(((String) data).trim());
            tvSimpleTextValue.setText("");
        } else if (data instanceof Pair) {
            Pair pair = (Pair) data;
            tvSimpleText.setText(((String) pair.first).trim());
            if (pair.second instanceof String) {
                tvSimpleTextValue.setText(((String) pair.second).trim());
            } else if (pair.second != null) {
                tvSimpleTextValue.setText(String.valueOf(pair.second).trim());
            } else {
//                vSplitter.setVisibility(View.GONE);
                tvSimpleTextValue.setVisibility(View.GONE);
                tvSimpleTextValue.setText("");
            }
        }
    }
}