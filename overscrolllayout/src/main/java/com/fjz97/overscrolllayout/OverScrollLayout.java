package com.fjz97.overscrolllayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 仿豆瓣书影音详情页横向滑动加载更多
 * <p>
 * Created by fjz on 2020/4/21.
 */
public class OverScrollLayout extends RelativeLayout {

    private static final String TAG = "OverScrollLayout";

    /**
     * 回弹时间
     */
    private int animDuration;

    /**
     * 弹出View的最大宽度
     */
    private int overScrollSize;

    /**
     * 弹出TextView变化的宽度
     */
    private int overScrollStateChangeSize;

    /**
     * 阻尼
     */
    private float damping;

    /**
     * 文本阻尼
     */
    private float textDamping;

    /**
     * 弹出文本
     */
    private String overScrollText;

    /**
     * 弹出变化文本
     */
    private String overScrollChangeText;

    /**
     * 文本字体大小
     */
    private float textSize;

    /**
     * 文本字体颜色
     */
    private int textColor;

    /**
     * 弹出View颜色
     */
    private int overScrollColor;

    /**
     * 子View
     */
    private RecyclerView mChildView;

    /**
     * 弹出View
     */
    private OverScrollView mOverScrollView;

    /**
     * 弹出TextView
     */
    private TextView mOverScrollTextView;

    /**
     * 子View初始位置
     */
    private Rect originalRect = new Rect();

    /**
     * 触摸时的横向偏移
     */
    private float startX;

    /**
     * 滑动的距离
     */
    private int scrollX;

    /**
     * 是否移动
     */
    private boolean isMoved;

    /**
     * 是否拦截触摸事件
     */
    private boolean intercept;

    /**
     * 是否可以overScroll
     */
    private boolean canOverScroll;

    /**
     * 释放回调
     */
    private OnOverScrollReleaseListener mOnOverScrollReleaseListener;

    public OverScrollLayout(Context context) {
        this(context, null);
    }

    public OverScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OverScrollLayout);
        canOverScroll = ta.getBoolean(R.styleable.OverScrollLayout_canOverScroll, true);
        animDuration = ta.getInteger(R.styleable.OverScrollLayout_animDuration, 400);
        overScrollSize = ta.getInteger(R.styleable.OverScrollLayout_overScrollSize, 120);
        overScrollStateChangeSize = ta.getInt(R.styleable.OverScrollLayout_overScrollStateChangeSize, 96);
        damping = ta.getFloat(R.styleable.OverScrollLayout_damping, .3f);
        textDamping = ta.getFloat(R.styleable.OverScrollLayout_textDamping, .2f);
        overScrollText = ta.getString(R.styleable.OverScrollLayout_overScrollText);
        overScrollChangeText = ta.getString(R.styleable.OverScrollLayout_overScrollChangeText);
        textSize = ta.getDimensionPixelSize(R.styleable.OverScrollLayout_textSize, 22);
        textColor = ta.getColor(R.styleable.OverScrollLayout_textColor, Color.parseColor("#CDCDCD"));
        overScrollColor = ta.getColor(R.styleable.OverScrollLayout_overScrollColor, Color.parseColor("#F5F5F5"));
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOverScrollView = new OverScrollView(getContext(), overScrollColor);
        mOverScrollTextView = new TextView(getContext());
        mOverScrollTextView.setEms(1);
        mOverScrollTextView.setLineSpacing(0, .8f);
        mOverScrollTextView.setText(overScrollText);
        mOverScrollTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mOverScrollTextView.setTextColor(textColor);
        addView(mOverScrollView);
        addView(mOverScrollTextView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mChildView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) instanceof RecyclerView) {
                    mChildView = (RecyclerView) getChildAt(i);
                }
            }
        }
        mChildView.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
        mOverScrollView.measure(MeasureSpec.makeMeasureSpec(overScrollSize,
                MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
        mOverScrollTextView.measure(MeasureSpec.makeMeasureSpec(overScrollSize,
                MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = mChildView.getMeasuredWidth();
        int height = mChildView.getMeasuredHeight();
        mChildView.layout(l, t, l + width, t + height);

        mOverScrollView.layout(r - mOverScrollView.getMeasuredWidth(), t, r, b);

        int textTop = (int) (height / 2f - mOverScrollTextView.getMeasuredHeight() / 2f);
        int textBottom = (int) (height / 2f + mOverScrollTextView.getMeasuredHeight() / 2f);
        mOverScrollTextView.layout(r, textTop, r + mOverScrollTextView.getMeasuredWidth(), textBottom);

        //设置初始位置
        originalRect.set(l, t, t + width, t + height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (isMoved) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        requestDisallowInterceptTouchEvent(true);//禁止父控件拦截事件

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //记录按下时的X
                startX = ev.getX();
            case MotionEvent.ACTION_MOVE:
                float nowX = ev.getX();
                scrollX = (int) (nowX - startX);
                if (isCanPullLeft() && scrollX < 0) {
                    int absScrollX = Math.abs((int) ((nowX - startX) * damping));
                    int textScrollX = Math.abs((int) ((nowX - startX) * textDamping));
                    mChildView.setTranslationX(-absScrollX);
                    if (absScrollX < overScrollSize) {
                        if (absScrollX >= overScrollStateChangeSize) {
                            mOverScrollTextView.setText(overScrollChangeText);
                        } else {
                            mOverScrollTextView.setText(overScrollText);
                        }
                        mOverScrollView.startOverScroll(overScrollSize - absScrollX, originalRect.top, overScrollSize + absScrollX, originalRect.bottom);
                        mOverScrollTextView.setTranslationX(-textScrollX);
                    }
                    isMoved = true;
                    intercept = false;
                    return true;
                } else {
                    startX = ev.getX();
                    isMoved = false;
                    intercept = true;
                    recoverLayout();
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    recoverLayout();
                }

                if (intercept) {
                    return super.dispatchTouchEvent(ev);
                } else {
                    return true;
                }
            default:
                return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 子View回归原位
     */
    private void recoverLayout() {
        if (!isMoved) {
            return;//如果没有移动布局，则跳过执行
        }

        mChildView.animate()
                .setDuration(animDuration)
                .translationX(-mChildView.getLeft());

        mOverScrollTextView.animate()
                .setDuration((long) (animDuration * (damping / textDamping)))
                .translationX(-scrollX * textDamping);

        ValueAnimator overViewAnim = ValueAnimator.ofInt(overScrollSize - mOverScrollView.left, 0);
        overViewAnim.setDuration(animDuration);
        overViewAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (int) animation.getAnimatedValue();
                mOverScrollView.startOverScroll(overScrollSize - offset, originalRect.top, overScrollSize + offset, originalRect.bottom);
            }
        });
        overViewAnim.start();

        //回调
        if (overScrollSize - mOverScrollView.left >= overScrollStateChangeSize) {
            if (mOnOverScrollReleaseListener != null) {
                mOnOverScrollReleaseListener.onRelease();
            }
        }
    }

    /**
     * 判断是否可以左拉
     */
    private boolean isCanPullLeft() {
        if (!canOverScroll) {
            return false;
        }

        final RecyclerView.Adapter adapter = mChildView.getAdapter();
        if (adapter == null) {
            return true;
        }
        final int lastItemPosition = adapter.getItemCount() - 1;
        final int lastVisiblePosition = ((LinearLayoutManager) mChildView.getLayoutManager()).findLastVisibleItemPosition();

        if (lastVisiblePosition >= lastItemPosition) {
            final int childIndex = lastVisiblePosition - ((LinearLayoutManager) mChildView.getLayoutManager()).findFirstVisibleItemPosition();
            final int childCount = mChildView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mChildView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getRight() + ((MarginLayoutParams) lastVisibleChild.getLayoutParams()).rightMargin
                        <= mChildView.getRight() - mChildView.getLeft();
            }
        }

        return false;
    }

    public void enableOverScroll() {
        canOverScroll = true;
    }

    public void disableOverScroll() {
        canOverScroll = false;
    }

    public int getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public int getOverScrollSize() {
        return overScrollSize;
    }

    public void setOverScrollSize(int overScrollSize) {
        this.overScrollSize = overScrollSize;
    }

    public int getOverScrollStateChangeSize() {
        return overScrollStateChangeSize;
    }

    public void setOverScrollStateChangeSize(int overScrollStateChangeSize) {
        this.overScrollStateChangeSize = overScrollStateChangeSize;
    }

    public float getDamping() {
        return damping;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public float getTextDamping() {
        return textDamping;
    }

    public void setTextDamping(float textDamping) {
        this.textDamping = textDamping;
    }

    public String getOverScrollText() {
        return overScrollText;
    }

    public void setOverScrollText(String overScrollText) {
        this.overScrollText = overScrollText;
    }

    public String getOverScrollChangeText() {
        return overScrollChangeText;
    }

    public void setOverScrollChangeText(String overScrollChangeText) {
        this.overScrollChangeText = overScrollChangeText;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getOverScrollColor() {
        return overScrollColor;
    }

    public void setOverScrollColor(int overScrollColor) {
        this.overScrollColor = overScrollColor;
    }

    public void setOnOverScrollReleaseListener(OnOverScrollReleaseListener onOverScrollReleaseListener) {
        mOnOverScrollReleaseListener = onOverScrollReleaseListener;
    }

    public interface OnOverScrollReleaseListener {
        void onRelease();
    }

    private class OverScrollView extends View {

        private Paint mOverScrollPaint;

        int left, top, right, bottom;

        public OverScrollView(Context context, int color) {
            super(context);

            mOverScrollPaint = new Paint();
            mOverScrollPaint.setStyle(Paint.Style.FILL);
            mOverScrollPaint.setAntiAlias(true);
            mOverScrollPaint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawArc(left, top, right, bottom, 0f, 360f, false, mOverScrollPaint);
        }

        public void startOverScroll(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            invalidate();
        }
    }
}
