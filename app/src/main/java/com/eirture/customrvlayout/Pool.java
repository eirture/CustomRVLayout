package com.eirture.customrvlayout;

import android.support.v4.util.SparseArrayCompat;

/**
 * Created by eirture on 16-10-17.
 */

public class Pool<T> {

    private SparseArrayCompat<T> mPool;
    private New<T> mNewInstance;

    public Pool(New<T> mNewInstance) {
        mPool = new SparseArrayCompat<>();
        this.mNewInstance = mNewInstance;
    }


    public T get(int key) {
        T res = mPool.get(key);
        if (res == null) {
            res = mNewInstance.get();
            mPool.put(key, res);
        }

        return res;
    }

    public interface New<T> {
        T get();
    }
}
