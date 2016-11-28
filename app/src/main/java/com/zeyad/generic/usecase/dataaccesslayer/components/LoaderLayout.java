package com.zeyad.generic.usecase.dataaccesslayer.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Layout used to wrap another content layout and to show status text and progress bar.
 */
public class LoaderLayout extends FrameLayout {

    private ProgressBar mProgressBar;
    private TextView tvStatus;

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context) {
        super(context);
        initView(context);
    }

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * {@inheritDoc}
     */
    public LoaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * Initializes views.
     *
     * @param context context.
     */
    private void initView(Context context) {
//        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_transparent));
        // inflate views.
//        LayoutInflater.from(context).inflate(R.layout.view_loader_layout, this, true);
//        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loader);
//        tvStatus = (TextView) findViewById(R.id.txv_status);
        showProgress();
    }

    /**
     * Show progress bar and hide another views.
     */
    public void showProgress() {
        mProgressBar.setVisibility(VISIBLE);
        tvStatus.setVisibility(GONE);
        setSubViewVisibility(INVISIBLE);
    }

    /**
     * Show progress bar over a semi transparent content.
     */
    public void showProgressOverContent() {
        mProgressBar.setVisibility(VISIBLE);
        bringChildToFront(mProgressBar);
        tvStatus.setVisibility(GONE);
        setSubViewVisibility(VISIBLE);
        setSubViewAlpha(0.5f);
    }

    /**
     * Show content views.
     */
    public void showContents() {
        mProgressBar.setVisibility(GONE);
        tvStatus.setVisibility(GONE);
        setSubViewVisibility(VISIBLE);
        setSubViewAlpha(1f);
    }

    /**
     * Show status and hide another views.
     *
     * @param status empty status to show.
     */
    public void showStatus(String status) {
        mProgressBar.setVisibility(GONE);
        tvStatus.setVisibility(VISIBLE);
        tvStatus.setText(status);
        setSubViewVisibility(INVISIBLE);
    }

    /**
     * Set visibility of sub views except text view and progress bar.
     *
     * @param visibility visibility of sub views.
     */
    private void setSubViewVisibility(int visibility) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != mProgressBar && view != tvStatus) {
                view.setVisibility(visibility);
            }
        }
    }

    /**
     * Set alpha of sub views except text view and progress bar.
     *
     * @param alpha alpha of sub views.
     */
    private void setSubViewAlpha(float alpha) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != mProgressBar && view != tvStatus)
                view.setAlpha(alpha);
        }
    }
}
