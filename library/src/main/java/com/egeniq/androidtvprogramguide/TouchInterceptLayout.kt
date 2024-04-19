package com.egeniq.androidtvprogramguide

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.egeniq.androidtvprogramguide.row.ProgramGuideRowGridView

class TouchInterceptLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var programGuideGridView: ProgramGuideRowGridView? = null

    fun setProgramGuideGridView(programGuideGridRowView: ProgramGuideRowGridView) {
        this.programGuideGridView = programGuideGridRowView
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Intercept touch events and pass only certain events to ProgramGuideGridView
        return when (ev?.actionMasked) {
            MotionEvent.ACTION_MOVE -> programGuideGridView?.onTouchEvent(ev) ?: false
            else -> super.onInterceptTouchEvent(ev)
        }
    }
}
