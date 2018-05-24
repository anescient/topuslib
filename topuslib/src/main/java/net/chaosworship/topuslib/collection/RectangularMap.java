package net.chaosworship.topuslib.collection;

import java.util.ArrayList;


public class RectangularMap<E> {

    private final ArrayList<ArrayList<E>> mRows;

    @SuppressWarnings("WeakerAccess")
    public RectangularMap(int width, int height) {
        if(width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        mRows = new ArrayList<>(width);
        for(int i = 0; i < width; i++) {
            ArrayList<E> row = new ArrayList<>(height);
            for(int j = 0; j < height; j++) {
                row.add(null);
            }
            mRows.add(row);
        }
    }

    public void clear() {
        for(ArrayList<E> row : mRows) {
            for(int j = 0; j < row.size(); j++) {
                row.set(j, null);
            }
        }
    }

    public void set(int x, int y, E value) {
        mRows.get(x).set(y, value);
    }

    public E get(int x, int y) {
        return mRows.get(x).get(y);
    }
}
