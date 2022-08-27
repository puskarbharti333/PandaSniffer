package com.pbharti.r64sniffer.utils;

import java.util.LinkedList;

public class ObjectPool<T> {
    private static final String TAG = "ObjectPool";
    private final LinkedList<T> mPool;
    private final IConstructor<T> mConstructor;
    private final int mSize;

    public ObjectPool(IConstructor<T> constructor, int size) {
        mPool = new LinkedList<>();
        mConstructor = constructor;
        mSize = size;
    }

    public T obtain(Object... params) {
        if (mConstructor == null) {
            return null;
        }

        synchronized (mPool) {
            T e = null;
            if (mPool.isEmpty()) {
                e = mConstructor.newInstance(params);
            } else {
                e = mPool.removeFirst();
            }

            mConstructor.initialize(e, params);

            return e;
        }
    }

    public boolean recycle(T o) {
        if (o != null) {
            synchronized (mPool) {
                if (mPool.size() > mSize) {
                    return false;
                }

                for (T t : mPool) {// prevent recycle multi times
                    if (t == o) {
                        return false;
                    }
                }

                return mPool.add(o);
            }
        }
        return false;
    }

    public interface IConstructor<T> {
        T newInstance(Object... params);

        void initialize(T e, Object... params);
    }

}
