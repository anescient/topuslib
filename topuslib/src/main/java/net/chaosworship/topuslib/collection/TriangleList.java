package net.chaosworship.topuslib.collection;

import net.chaosworship.topuslib.geom2d.Triangle;

import java.util.Arrays;


public class TriangleList implements TriangleConsumer {

    private Triangle[] mTriangles;
    private int mTriangleCount;

    public TriangleList() {
        mTriangles = new Triangle[20];
        mTriangleCount = 0;
    }

    public void clear() {
        mTriangleCount = 0;
    }

    @Override
    public void putTriangle(Triangle triangle) {
        if(mTriangles.length < mTriangleCount + 1) {
            mTriangles = Arrays.copyOf(mTriangles, 3 * mTriangles.length / 2 + 1);
        }
        mTriangles[mTriangleCount++] = triangle;
    }

    public void putTriangles(TriangleConsumer consumer) {
        for(int i = 0; i < mTriangleCount; i++) {
            consumer.putTriangle(mTriangles[i]);
        }
    }
}
