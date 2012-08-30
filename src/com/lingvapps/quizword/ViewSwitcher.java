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
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            if (mScroller.isFinished()) {
                mLastMotionX = ev.getX();
                mTouchState = TOUCH_STATE_REST;
            }
            return false;
        case MotionEvent.ACTION_UP:
            return false;
        default:
            return true;
        }
    }
}
