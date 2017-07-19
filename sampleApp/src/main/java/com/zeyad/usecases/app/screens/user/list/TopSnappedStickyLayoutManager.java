package com.zeyad.usecases.app.screens.user.list;

import android.content.Context;

import com.zeyad.gadapter.stickyheaders.StickyLayoutManager;
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler;

public final class TopSnappedStickyLayoutManager extends StickyLayoutManager {

    TopSnappedStickyLayoutManager(Context context, StickyHeaderHandler headerHandler) {
        super(context, headerHandler);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPositionWithOffset(position, 0);
    }
}
