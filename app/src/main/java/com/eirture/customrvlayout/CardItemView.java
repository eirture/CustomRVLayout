package com.eirture.customrvlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by qibin on 16-9-26.
 */

public class CardItemView extends View {

    private int mSize;
    private int lineLength;
    private int mHeight;
    private Paint mPaint;
    private Path mDrawPath;
    private Region mRegion;

    public CardItemView(Context context) {
        this(context, null, 0);
    }

    public CardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Card, defStyleAttr, 0);
        mSize = ta.getDimensionPixelSize(R.styleable.Card_size, 10);
        lineLength = mSize / 4;
        mHeight = (int) (Math.sqrt(3) / 2 * mSize);
        mPaint.setColor(ta.getColor(R.styleable.Card_bgColor, 0));
        ta.recycle();

        mRegion = new Region();
        mDrawPath = new Path();

        mDrawPath.moveTo(lineLength, 0);
        mDrawPath.lineTo(lineLength * 3, 0);
        mDrawPath.lineTo(mSize, mHeight / 2);
        mDrawPath.lineTo(lineLength * 3, mHeight);
        mDrawPath.lineTo(lineLength, mHeight);
        mDrawPath.lineTo(0, mHeight / 2);
        mDrawPath.close();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize, mHeight);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isEventInPath(event)) {
                return false;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean isEventInPath(MotionEvent event) {
        RectF bounds = new RectF();
        mDrawPath.computeBounds(bounds, true);
        mRegion.setPath(mDrawPath, new Region((int) bounds.left,
                (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return mRegion.contains((int) event.getX(), (int) event.getY());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawPath(mDrawPath, mPaint);
    }

    public void setCardColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}
