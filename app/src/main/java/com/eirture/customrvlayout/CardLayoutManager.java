package com.eirture.customrvlayout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by eirture on 16-10-17.
 */
public class CardLayoutManager extends RecyclerView.LayoutManager {
    public static final int DEFAULT_GROUP_SIZE = 5;

    private int mGroupSize;
    private int mHorizontalOffset;
    private int mVerticalOffset;
    private int mTotalWidth;
    private int mTotalHeight;
    private int mGravityOffset;
    private boolean isGravityCenter;

    private Pool<Rect> mItemFrames;

    public CardLayoutManager(int groupSize, boolean center) {
        mGroupSize = groupSize;
        isGravityCenter = center;
        mItemFrames = new Pool<>(new Pool.New<Rect>() {
            @Override
            public Rect get() {
                return new Rect();
            }
        });
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        detachAndScrapAttachedViews(recycler);
        View first = recycler.getViewForPosition(0);
        measureChildWithMargins(first, 0, 0);
        int itemWidth = getDecoratedMeasuredWidth(first);
        int itemHeight = getDecoratedMeasuredHeight(first);

        int firstLineSize = mGroupSize / 2 + 1;
        if (isGravityCenter && firstLineSize * itemWidth < getHorizontalSpace()) {
            mGravityOffset = (getHorizontalSpace() - firstLineSize * itemWidth - (mGroupSize - firstLineSize) * itemWidth / 2) / 2;
        } else {
            mGravityOffset = 0;
        }

        for (int i = 0; i < getItemCount(); i++) {
            Rect item = mItemFrames.get(i);
            float coefficient = isFirstGroup(i) ? 1.5f : 1f;
            int offsetHeight = (int) ((i / mGroupSize) * itemHeight * coefficient);

            int left, right, top, bottom;
            top = offsetHeight;
            if (isItemInFirstLine(i)) {
                left = itemWidth * (i % mGroupSize);
            } else {
                top += itemHeight / 2;
                int size = i % mGroupSize - firstLineSize;
                left = size * itemWidth + itemWidth / 4 * 3;
            }


            left += itemWidth / 2 * ((i % mGroupSize) % firstLineSize);
            left += mGravityOffset; // 加上水平偏移量

            bottom = top + itemHeight;
            right = left + itemWidth;
            item.set(left, top, right, bottom);

        }

        mTotalWidth = Math.max(firstLineSize * itemWidth + (mGroupSize - firstLineSize) * itemHeight / 2, getHorizontalSpace());
        int totalHeight = getGroupSize() * itemHeight;
        if (!isItemInFirstLine(getItemCount() - 1)) {
            totalHeight += itemHeight / 2;
        }
        mTotalHeight = Math.max(totalHeight, getVerticalSpace());
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        Rect displayRect = new Rect(mHorizontalOffset, mVerticalOffset,
                getHorizontalSpace() + mHorizontalOffset,
                getVerticalSpace() + mVerticalOffset);

        Rect rect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            rect.left = getDecoratedLeft(item);
            rect.top = getDecoratedTop(item);
            rect.right = getDecoratedRight(item);
            rect.bottom = getDecoratedBottom(item);
            if (!Rect.intersects(displayRect, rect)) {
                removeAndRecycleView(item, recycler);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            Rect frame = mItemFrames.get(i);
            if (Rect.intersects(displayRect, frame)) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                layoutDecorated(scrap, frame.left - mHorizontalOffset, frame.top - mVerticalOffset,
                        frame.right - mHorizontalOffset, frame.bottom - mVerticalOffset);
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mVerticalOffset + dy < 0) {
            dy = -mVerticalOffset;
        } else if (mVerticalOffset + dy > mTotalHeight - getVerticalSpace()) {
            dy = mTotalHeight - getVerticalSpace() - mVerticalOffset;
        }

        offsetChildrenVertical(-dy);
        fill(recycler, state);
        mVerticalOffset += dy;
        return dy;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mHorizontalOffset + dx < 0) {
            dx = -mHorizontalOffset;
        } else if (mHorizontalOffset + dx > mTotalWidth - getHorizontalSpace()) {
            dx = mTotalWidth - getHorizontalSpace() - mHorizontalOffset;
        }

        offsetChildrenHorizontal(-dx);
        fill(recycler, state);
        mHorizontalOffset += dx;
        return dx;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }


    private boolean isItemInFirstLine(int i) {
        return i % mGroupSize < mGroupSize / 2 + 1;
    }

    //判断是否是双行，最后一组可能为单行
    private boolean isFirstGroup(int i) {
        return i < mGroupSize;
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public int getGroupSize() {
        return (int) Math.ceil(getItemCount() / mGroupSize);
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
