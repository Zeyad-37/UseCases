package com.zeyad.usecases.app.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;

public class ScrollEventCalculator {

    private ScrollEventCalculator() {
    }

    /**
     * Determine if the scroll event at the end of the recycler view.
     *
     * @return true if at end of linear list recycler view, false otherwise.
     */
    public static boolean isAtScrollEnd(RecyclerViewScrollEvent recyclerViewScrollEvent) {
        RecyclerView.LayoutManager layoutManager = recyclerViewScrollEvent.view().getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return linearLayoutManager.getItemCount() <= (linearLayoutManager.findLastVisibleItemPosition() + 2);
        }
        return false;
    }
}
