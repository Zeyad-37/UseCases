package com.zeyad.usecases.app.components

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.zeyad.usecases.app.R

/**
 * Layout used to wrap another content layout and to show status text and progress bar.
 */
class LoaderLayout : FrameLayout, View.OnClickListener {

    private var mProgressBar: ProgressBar? = null
    private var mTxvStatus: TextView? = null
    private var mStatusTextListener: StatusTextListener? = null
    private var mBlockTouch: Boolean = false

    /** {@inheritDoc}  */
    constructor(context: Context) : super(context) {
        initView(context)
    }

    /** {@inheritDoc}  */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    /** {@inheritDoc}  */
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    /**
     * Initializes views.
     *
     * @param context context.
     */
    private fun initView(context: Context) {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_transparent))
        // inflate views.
        LayoutInflater.from(context).inflate(R.layout.view_loader_layout, this, true)
        mProgressBar = findViewById<View>(R.id.progress_bar_loader) as ProgressBar
        mTxvStatus = findViewById<View>(R.id.txv_status) as TextView
        // set click listener
        mTxvStatus!!.setOnClickListener(this)
        //        showProgress();
    }

    /**
     * Set click listener on status text.
     *
     * @param mStatusTextListener status text listener.
     */
    fun setStatusTextListener(mStatusTextListener: StatusTextListener) {
        this.mStatusTextListener = mStatusTextListener
    }

    /** {@inheritDoc}  */
    override fun onClick(view: View) {
        if (view.id == R.id.txv_status && mStatusTextListener != null) {
            mStatusTextListener!!.onStatusTextClick()
        }
    }

    /** {@inheritDoc}  */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mBlockTouch
    }

    /** {@inheritDoc}  */
    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
        return mBlockTouch
    }

    /** Show progress bar and hide another views.  */
    fun showProgress() {
        mProgressBar!!.bringToFront()
        mProgressBar!!.visibility = View.VISIBLE
        mTxvStatus!!.visibility = View.GONE
        setSubViewVisibility(View.INVISIBLE)
        mBlockTouch = true
    }

    /** Show progress bar over a semi transparent content.  */
    fun showProgressOverContent() {
        mProgressBar!!.bringToFront()
        mProgressBar!!.visibility = View.VISIBLE
        bringChildToFront(mProgressBar)
        mTxvStatus!!.visibility = View.GONE
        setSubViewVisibility(View.VISIBLE)
        setSubViewAlpha(0.5f)
        mBlockTouch = true
    }

    /** Show content views.  */
    fun showContents() {
        mProgressBar!!.visibility = View.GONE
        mTxvStatus!!.visibility = View.GONE
        setSubViewVisibility(View.VISIBLE)
        setSubViewAlpha(1f)
        mBlockTouch = false
    }

    /**
     * Show status and hide another views.
     *
     * @param status empty status to show.
     */
    fun showStatus(status: String) {
        mProgressBar!!.visibility = View.GONE
        mTxvStatus!!.visibility = View.VISIBLE
        mTxvStatus!!.text = status
        setSubViewVisibility(View.INVISIBLE)
        mBlockTouch = false
    }

    /**
     * Set visibility of sub views except text view and progress bar.
     *
     * @param visibility visibility of sub views.
     */
    private fun setSubViewVisibility(visibility: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view != mProgressBar && view != mTxvStatus) {
                view.visibility = visibility
            }
        }
    }

    /**
     * Set alpha of sub views except text view and progress bar.
     *
     * @param alpha alpha of sub views.
     */
    private fun setSubViewAlpha(alpha: Float) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view != mProgressBar && view != mTxvStatus) {
                view.alpha = alpha
            }
        }
    }

    /** Interface to listen event of status text click.  */
    interface StatusTextListener {
        /** Called when status text clicked.  */
        fun onStatusTextClick()
    }
}
