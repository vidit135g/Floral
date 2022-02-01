package com.absolute.floral.ui.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

//Solution heavily inspired by:
//https://github.com/WangDaYeeeeee/Mysplash/blob/master/app/src/main/java/com/wangdaye/mysplash/common/ui/widget/SwipeBackCoordinatorLayout.java
public class SwipeBackCoordinatorLayout extends CoordinatorLayout {
    // widget
    public OnSwipeListener listener;

    // data
    private int swipeDistance = 0;
    private static float SWIPE_TRIGGER = 100;
    private static final float SWIPE_RADIO = 2.5F;

    private int swipeDir = NULL_DIR;
    public static final int NULL_DIR = 0;
    public static final int UP_DIR = 1;
    public static final int DOWN_DIR = -1;

    public SwipeBackCoordinatorLayout(Context context) {
        super(context);
        this.initialize();
    }

    public SwipeBackCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SwipeBackCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        SWIPE_TRIGGER = (float) (getResources().getDisplayMetrics().heightPixels / 5.0);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int axes, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            return super.onStartNestedScroll(child, target, axes, type)
                    || ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0);
        }
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        int dyConsumed = 0;
        if (swipeDistance != 0) {
            dyConsumed = onPreScroll(dy);
        }

        int[] newConsumed = new int[]{0, 0};
        super.onNestedPreScroll(target, dx, dy - dyConsumed, newConsumed, type);

        consumed[0] = newConsumed[0];
        consumed[1] = newConsumed[1] + dyConsumed;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        int newDyConsumed = dyConsumed;
        int newDyUnconsumed = dyUnconsumed;
        if (swipeDistance == 0) {
            int dir = dyUnconsumed < 0 ? DOWN_DIR : UP_DIR;
            if (listener.canSwipeBack(dir)) {
                onScroll(dyUnconsumed);
                newDyConsumed = dyConsumed + dyUnconsumed;
                newDyUnconsumed = 0;
            }
        }

        super.onNestedScroll(target, dxConsumed, newDyConsumed, dxUnconsumed, newDyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(View child, int type) {
        super.onStopNestedScroll(child, type);
        if (Math.abs(swipeDistance) >= SWIPE_TRIGGER) {
            swipeBack();
        } else {
            reset();
        }
    }

    private int onPreScroll(int dy) {
        int consumed;
        if (swipeDistance * (swipeDistance - dy) < 0) {
            swipeDir = NULL_DIR;
            consumed = swipeDistance;
            swipeDistance = 0;
        } else {
            consumed = dy;
            swipeDistance -= dy;
        }

        setSwipeTranslation();

        return consumed;
    }

    private void onScroll(int dy) {
        swipeDistance = -dy;
        swipeDir = swipeDistance > 0 ? DOWN_DIR : UP_DIR;

        setSwipeTranslation();
    }


    private void swipeBack() {
        if (listener != null) {
            listener.onSwipeFinish(swipeDir);
        }
    }

    private void reset() {
        swipeDir = NULL_DIR;
        if (swipeDistance != 0) {
            ResetAnimation a = new ResetAnimation(swipeDistance);
            a.setAnimationListener(resetAnimListener);
            a.setDuration(300);
            startAnimation(a);
        }
    }

    private void setSwipeTranslation() {
        float translationY = (float) (1.0 * swipeDistance / SWIPE_RADIO);
        setTranslationY(translationY);
        if (listener != null) {
            listener.onSwipeProcess(Math.min(1, Math.abs(translationY)));
        }
    }

    public static int getBackgroundColor(float percent) {
        return Color.argb((int) (255 * 0.5 * (2 - percent)), 0, 0, 0);
    }

    public static boolean canSwipeBackForThisView(View v, int dir) {
        return !v.canScrollVertically(dir);
    }

    private class ResetAnimation extends Animation {
        // data
        private int fromDistance;

        ResetAnimation(int from) {
            this.fromDistance = from;
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            swipeDistance = (int) (fromDistance * (1 - interpolatedTime));
            setSwipeTranslation();
        }
    }

    private Animation.AnimationListener resetAnimListener
            = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setEnabled(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    };

    public interface OnSwipeListener {
        boolean canSwipeBack(int dir);

        void onSwipeProcess(float percent);

        void onSwipeFinish(int dir);
    }

    public void setOnSwipeListener(OnSwipeListener l) {
        this.listener = l;
    }
}
