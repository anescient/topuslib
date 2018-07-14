package net.chaosworship.topuslib.geom2d.rangesearch;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.tuple.PointObjectPair;

import java.util.Arrays;
import java.util.Collection;


class DoublySortedPointObjects {

    private static final int INITIALCAPACITY = 100;

    private PointObjectPair mPointObjectsByX[];
    private PointObjectPair mPointObjectsByY[];
    private int mCount;

    DoublySortedPointObjects() {
        mPointObjectsByX = new PointObjectPair[INITIALCAPACITY];
        mPointObjectsByY = new PointObjectPair[INITIALCAPACITY];
        mCount = 0;
    }

    int size() {
        return mCount;
    }

    boolean isEmpty() {
        return mCount == 0;
    }

    private void ensureCapacity(int capacity) {
        if(mPointObjectsByX.length < capacity) {
            mPointObjectsByX = Arrays.copyOf(mPointObjectsByX, capacity);
            mPointObjectsByY = Arrays.copyOf(mPointObjectsByY, capacity);
        }
    }

    PointObjectPair getSingle() {
        if(BuildConfig.DEBUG && mCount != 1) {
            throw new AssertionError();
        }
        return mPointObjectsByX[0];
    }

    void set(Collection<PointObjectPair> pointObjects) {
        ensureCapacity(pointObjects.size());
        mCount = 0;
        for(PointObjectPair pop : pointObjects) {
            mPointObjectsByX[mCount] = pop;
            mPointObjectsByY[mCount] = pop;
            mCount++;
        }
    }

    void sort() {
        Arrays.sort(mPointObjectsByX, 0, mCount, PointObjectPair.compareXY);
        Arrays.sort(mPointObjectsByY, 0, mCount, PointObjectPair.compareYX);
    }

    <T> void getValuesInRect(Rectangle rect, Collection<T> searchResults) {
        for(int i = 0; i < mCount; i++) {
            PointObjectPair pop = mPointObjectsByY[i];
            if(rect.contains(pop.point)) {
                //noinspection unchecked
                searchResults.add((T)pop.value);
            }
        }
    }

    <T> void getAllValues(Collection<T> searchResults) {
        for(int i = 0; i < mCount; i++) {
            //noinspection unchecked
            searchResults.add((T)mPointObjectsByY[i].value);
        }
    }

    void getAll(Collection searchResults) {
        for(int i = 0; i < mCount; i++) {
            searchResults.add(mPointObjectsByX[i]);
        }
    }

    private void splitOnX(DoublySortedPointObjects lesser, DoublySortedPointObjects greater, float splitX) {
        lesser.mCount = 0;
        greater.mCount = 0;
        lesser.ensureCapacity(mCount); // yes, this allocates too much, eat me
        greater.ensureCapacity(mCount);

        for(int i = 0; i < mCount; i++) {
            PointObjectPair pop = mPointObjectsByX[i];
            if(pop.point.x <= splitX) {
                lesser.mPointObjectsByX[lesser.mCount++] = pop;
            } else {
                greater.mPointObjectsByX[greater.mCount++] = pop;
            }
        }

        int lesserCount = 0;
        int greaterCount = 0;

        for(int i = 0; i < mCount; i++) {
            PointObjectPair pop = mPointObjectsByY[i];
            if(pop.point.x <= splitX) {
                lesser.mPointObjectsByY[lesserCount++] = pop;
            } else {
                greater.mPointObjectsByY[greaterCount++] = pop;
            }
        }

        if(lesserCount != lesser.mCount || greaterCount != greater.mCount) {
            throw new AssertionError();
        }
    }

    float splitOnX(DoublySortedPointObjects lesser, DoublySortedPointObjects greater) {
        if(mCount < 2) {
            if(mCount == 0) {
                throw new AssertionError();
            }
            greater.mCount = 0;
            lesser.mCount = 1;
            lesser.mPointObjectsByX[0] = mPointObjectsByX[0];
            return mPointObjectsByX[0].point.x;
        }

        float medianX = mPointObjectsByX[mCount / 2 - 1].point.x;
        splitOnX(lesser, greater, medianX);

        if(greater.mCount == 0) {
            medianX = (mPointObjectsByX[0].point.x + mPointObjectsByX[mCount - 1].point.x) / 2;
            splitOnX(lesser, greater, medianX);
        }

        return medianX;
    }

    private void splitOnY(DoublySortedPointObjects lesser, DoublySortedPointObjects greater, float splitY) {
        lesser.mCount = 0;
        greater.mCount = 0;
        lesser.ensureCapacity(mCount);
        greater.ensureCapacity(mCount);

        for(int i = 0; i < mCount; i++) {
            PointObjectPair pop = mPointObjectsByX[i];
            if(pop.point.y <= splitY) {
                lesser.mPointObjectsByX[lesser.mCount++] = pop;
            } else {
                greater.mPointObjectsByX[greater.mCount++] = pop;
            }
        }

        int lesserCount = 0;
        int greaterCount = 0;
        for(int i = 0; i < mCount; i++) {
            PointObjectPair pop = mPointObjectsByY[i];
            if(pop.point.y <= splitY) {
                lesser.mPointObjectsByY[lesserCount++] = pop;
            } else {
                greater.mPointObjectsByY[greaterCount++] = pop;
            }
        }

        if(lesserCount != lesser.mCount || greaterCount != greater.mCount) {
            throw new AssertionError();
        }
    }

    float splitOnY(DoublySortedPointObjects lesser, DoublySortedPointObjects greater) {
        if(mCount < 2) {
            if(mCount == 0) {
                throw new AssertionError();
            }
            greater.mCount = 0;
            lesser.mCount = 1;
            lesser.mPointObjectsByY[0] = mPointObjectsByY[0];
            return mPointObjectsByY[0].point.y;
        }


        float medianY = mPointObjectsByY[mCount / 2 - 1].point.y;
        splitOnY(lesser, greater, medianY);

        if(greater.mCount == 0) {
            medianY = (mPointObjectsByY[0].point.y + mPointObjectsByY[mCount - 1].point.y) / 2;
            splitOnY(lesser, greater, medianY);
        }

        return medianY;
    }
}
