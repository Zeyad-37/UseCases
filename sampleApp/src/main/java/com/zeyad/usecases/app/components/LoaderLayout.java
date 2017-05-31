package com.zeyad.usecases.app.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zeyad.usecases.app.R;

/** Layout used to wrap another content layout and to show status text and progress bar. */
public class LoaderLayout extends FrameLayout implements View.OnClickListener {

    private ProgressBar mProgressBar;
    private TextView mTxvStatus;
    private StatusTextListener mStatusTextListener;
    private boolean mBlockTouch;

    /** {@inheritDoc} */
    public LoaderLayout(Context context) {
        super(context);
        initView(context);
    }

    /** {@inheritDoc} */
    public LoaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /** {@inheritDoc} */
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
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_transparent));
        // inflate views.
        LayoutInflater.from(context).inflate(R.layout.view_loader_layout, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loader);
        mTxvStatus = (TextView) findViewById(R.id.txv_status);
        // set click listener
        mTxvStatus.setOnClickListener(this);
        //        showProgress();
    }

    /**
     * Set click listener on status text.
     *
     * @param mStatusTextListener status text listener.
     */
    public void setStatusTextListener(StatusTextListener mStatusTextListener) {
        this.mStatusTextListener = mStatusTextListener;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txv_status && mStatusTextListener != null) {
            mStatusTextListener.onStatusTextClick();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mBlockTouch;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mBlockTouch;
    }

    /** Show progress bar and hide another views. */
    public void showProgress() {
        mProgressBar.bringToFront();
        mProgressBar.setVisibility(VISIBLE);
        mTxvStatus.setVisibility(GONE);
        setSubViewVisibility(INVISIBLE);
        mBlockTouch = true;
    }

    /** Show progress bar over a semi transparent content. */
    public void showProgressOverContent() {
        mProgressBar.bringToFront();
        mProgressBar.setVisibility(VISIBLE);
        bringChildToFront(mProgressBar);
        mTxvStatus.setVisibility(GONE);
        setSubViewVisibility(VISIBLE);
        setSubViewAlpha(0.5f);
        mBlockTouch = true;
    }

    /** Show content views. */
    public void showContents() {
        mProgressBar.setVisibility(GONE);
        mTxvStatus.setVisibility(GONE);
        setSubViewVisibility(VISIBLE);
        setSubViewAlpha(1f);
        mBlockTouch = false;
    }

    /**
     * Show status and hide another views.
     *
     * @param status empty status to show.
     */
    public void showStatus(String status) {
        mProgressBar.setVisibility(GONE);
        mTxvStatus.setVisibility(VISIBLE);
        mTxvStatus.setText(status);
        setSubViewVisibility(INVISIBLE);
        mBlockTouch = false;
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
            if (!view.equals(mProgressBar) && !view.equals(mTxvStatus)) {
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
            if (!view.equals(mProgressBar) && !view.equals(mTxvStatus)) {
                view.setAlpha(alpha);
            }
        }
    }

    /** Interface to listen event of status text click. */
    public interface StatusTextListener {
        /** Called when status text clicked. */
        void onStatusTextClick();
    }
}
