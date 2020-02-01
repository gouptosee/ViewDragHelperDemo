package com.example.viewdemo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

public class FloatLayout extends FrameLayout {

    private final int NONE = -1;
    private View dragView;
    private ViewDragHelper viewDragHelper;
    private int mWidth;
    private int mHeight;
    private int mChildWidth;
    private int mChildHeight;
    private boolean onDrag = true;
    private boolean dragEnable = true;
    private boolean sideEnable = true;  //是否吸边

    private int lastChildX;
    private int lastChildY;

    private int topFinalOffset;
    private int bottomFinalOffset;
    private int leftFinalOffset;
    private int rightFinalOffset;


    private int leftDragOffset = NONE;
    private int rightDragOffset = NONE;
    private int topDragOffset = NONE;
    private int bottomDragOffset = NONE;

    public FloatLayout(@NonNull Context context) {
        this(context, null);
    }

    public FloatLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        viewDragHelper = ViewDragHelper.create(this, new MyDragCallBack());
    }

    public void setBottomDragOffset(int dpValue) {
        this.bottomDragOffset = dp2px(getContext(), dpValue);
    }

    public void setTopDragOffset(int dpValue) {
        this.topDragOffset = dp2px(getContext(), dpValue);
    }

    public void setLeftDragOffset(int dpValue) {
        this.leftDragOffset = dp2px(getContext(), dpValue);
    }

    public void setRightDragOffset(int dpValue) {
        this.rightDragOffset = dp2px(getContext(), dpValue);
    }

    public void setFinalOffsets(int value) {
        setFinalOffsets(value, value, value, value);
    }

    //拖拽能偏移出父容器的值，取正数
    public void setFinalDragOffsets(int left, int top, int right, int bottom) {
        setLeftDragOffset(left);
        setTopDragOffset(top);
        setRightDragOffset(right);
        setBottomDragOffset(bottom);
    }

    public void setFinalOffsets(int left, int top, int right, int bottom) {
        setLeftFinalOffset(left);
        setTopFinalOffset(top);
        setRightFinalOffset(right);
        setBottomFinalOffset(bottom);
//        calLayoutOffset();
    }

    public void setLeftFinalOffset(int dpValue) {
        this.leftFinalOffset = dp2px(getContext(), dpValue);
    }

    public void setRightFinalOffset(int dpValue) {
        this.rightFinalOffset = dp2px(getContext(), dpValue);
    }

    public void setBottomFinalOffset(int dpValue) {
        this.bottomFinalOffset = dp2px(getContext(), dpValue);
    }

    public void setTopFinalOffset(int dpValue) {
        this.topFinalOffset = dp2px(getContext(), dpValue);
    }

    public void enableDrag(boolean value) {
        dragEnable = value;
    }

    public void enableSide(boolean value) {
        sideEnable = value;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mChildHeight = dragView.getMeasuredHeight();
        mChildWidth = dragView.getMeasuredWidth();

        leftDragOffset = leftDragOffset == NONE ? mChildWidth / 2 : leftDragOffset;
        rightDragOffset = rightDragOffset == NONE ? mChildWidth / 2 : rightDragOffset;
        topDragOffset = topDragOffset == NONE ? mChildHeight / 2 : topDragOffset;
        bottomDragOffset = bottomDragOffset == NONE ? mChildHeight / 2 : bottomDragOffset;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (lastChildX == 0 && lastChildY == 0) {
            calLayoutOffset();
        }
        dragView.layout(lastChildX, lastChildY, lastChildX + mChildWidth, lastChildY + mChildHeight);
    }

    public void calLayoutOffset() {
        lastChildX =leftFinalOffset;
        lastChildY =topFinalOffset;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new RuntimeException("child size must be 1");
        }
        dragView = getChildAt(0);
        dragView.bringToFront();
    }


    private class MyDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return dragView == view;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            leftDragOffset = leftDragOffset > mChildWidth ? mChildWidth : leftDragOffset;
            rightDragOffset = rightDragOffset > mChildWidth ? mChildWidth : rightDragOffset;

            return clamp(left, -leftDragOffset, mWidth - mChildWidth + rightDragOffset);

        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            topDragOffset = topDragOffset > mChildHeight ? mChildHeight : topDragOffset;
            bottomDragOffset = bottomDragOffset > mChildHeight ? mChildHeight : bottomDragOffset;

            return clamp(top, -topDragOffset, mHeight - mChildHeight + bottomDragOffset);

        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
//            return super.getViewVerticalDragRange(child);
            return mHeight;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
//            return super.getViewHorizontalDragRange(child);
            return mWidth;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (sideEnable) {
                super.onViewReleased(releasedChild, xvel, yvel);

                int finalTop = dragView.getTop() <= topFinalOffset ? topFinalOffset : dragView.getBottom() >= mHeight - bottomFinalOffset ? mHeight - dragView.getMeasuredHeight() - bottomFinalOffset : dragView.getTop();
                lastChildY = finalTop;
                if (Math.abs(dragView.getLeft()) <= (getMeasuredWidth() - dragView.getMeasuredWidth()) / 2) {
                    lastChildX = leftFinalOffset;
                    viewDragHelper.settleCapturedViewAt(lastChildX, finalTop);
                } else {
                    lastChildX = getMeasuredWidth() - dragView.getMeasuredWidth() - rightFinalOffset;
                    viewDragHelper.settleCapturedViewAt(lastChildX,
                            finalTop);
                }
                invalidate();
            } else {
                lastChildX = dragView.getLeft();
                lastChildY = dragView.getTop();
            }
            onDrag = false;


        }
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }


    private Rect mRect = new Rect();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (dragEnable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    dragView.getHitRect(mRect);
                    onDrag = mRect.contains(x, y);
                    break;
            }

            if (onDrag) return viewDragHelper.shouldInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragEnable) {
            if (onDrag) {
                viewDragHelper.processTouchEvent(event);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (dragEnable) {
            if (viewDragHelper.continueSettling(true)) {
                invalidate();
            }
        }
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
