package com.iutcalendar.swiping

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

open class GestureEventManager : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onClick()
        return super.onSingleTapUp(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleClick()
        return super.onDoubleTap(e)
    }

    override fun onLongPress(e: MotionEvent) {
        onLongClick()
        super.onLongPress(e)
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown()
                    } else {
                        onSwipeUp()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    protected fun onSwipeUp() {}
    open fun onSwipeDown() {}
    protected open fun onClick() {}
    protected fun onDoubleClick() {}
    protected fun onLongClick() {}

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}