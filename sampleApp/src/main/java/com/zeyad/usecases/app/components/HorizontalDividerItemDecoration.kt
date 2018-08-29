package com.zeyad.usecases.app.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

class HorizontalDividerItemDecoration
/**
 * Custom divider will be used
 */
(context: Context, resId: Int) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable?

    init {
        mDivider = ContextCompat.getDrawable(context, resId)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider.setBounds(parent.paddingLeft, top, parent.width - parent.paddingRight,
                    bottom)
            mDivider.draw(c)
        }
    }
}
