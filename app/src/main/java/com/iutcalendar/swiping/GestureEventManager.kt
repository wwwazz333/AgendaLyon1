package com.iutcalendar.swiping

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

open class GestureEventManager : GestureDetector.OnGestureListener {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(p0: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onClick()
        return true
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        Log.d("onScroll", "scroll : ($p2, $p3")
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        onLongClick()
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
            } else {
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
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
    private fun onSwipeUp() {}
    open fun onSwipeDown() {}
    protected open fun onClick() {}
    private fun onDoubleClick() {}
    private fun onLongClick() {}


    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}