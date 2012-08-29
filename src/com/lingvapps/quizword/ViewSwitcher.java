package com.lingvapps.quizword;

import android.content.Context;
import android.view.MotionEvent;
import de.marcreichelt.android.RealViewSwitcher;

public class ViewSwitcher extends RealViewSwitcher {

    public ViewSwitcher(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScroller.isFinished()) {
            return true;
        }
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionX = ev.getX();
            mTouchState = TOUCH_STATE_REST;
            return false;
        case MotionEvent.ACTION_UP:
            return false;
        default:
            return true;
        }
    }
}
