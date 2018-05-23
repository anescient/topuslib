package net.chaosworship.topuslib.collection;

import java.util.ArrayList;


public class RectangularMap<E> {

    private final ArrayList<ArrayList<E>> mMap;
    private final int mWidth;
    private final int mHeight;

    public RectangularMap(int width, int height) {
        if(width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        mWidth = width;
        mHeight = height;
        mMap = new ArrayList<>(mWidth);
        for(int i = 0; i < mWidth; i++) {
            ArrayList<E> row = new ArrayList<>(mHeight);
            for(int j = 0; j < mHeight; j++) {
                row.add(null);
            }
            mMap.add(row);
        }
    }

    public void clear() {
        for(int i = 0; i < mWidth; i++) {
            ArrayList<E> row = mMap.get(i);
            for(int j = 0; j < mHeight; j++) {
                row.set(j, null);
            }
        }
    }

    public void set(int x, int y, E value) {
        mMap.get(x).set(y, value);
    }

    public E get(int x, int y) {
        return mMap.get(x).get(y);
    }
}
