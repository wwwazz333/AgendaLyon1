package com.iutcalendar.swiping;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TouchGestureListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public TouchGestureListener(Context ctx, GestureEventManager gs) {
        gestureDetector = new GestureDetector(ctx, gs);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return gestureDetector.onTouchEvent(event);
    }

}