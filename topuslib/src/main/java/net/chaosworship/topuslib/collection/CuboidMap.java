package net.chaosworship.topuslib.collection;

import java.util.ArrayList;


public class CuboidMap<E> {

    private final ArrayList<RectangularMap<E>> mLayers;

    public CuboidMap(int width, int height, int depth) {
        mLayers = new ArrayList<>(depth);
        for(int k = 0; k < depth; k++) {
            mLayers.add(new RectangularMap<E>(width, height));
        }
    }

    public void clear() {
        for(RectangularMap<E> layer : mLayers) {
            layer.clear();
        }
    }

    public void set(int x, int y, int z, E value) {
        mLayers.get(z).set(x, y, value);
    }

    public E get(int x, int y, int z) {
        return mLayers.get(z).get(x, y);
    }
}
