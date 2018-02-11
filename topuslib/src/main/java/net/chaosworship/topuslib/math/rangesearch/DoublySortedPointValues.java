package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.BuildConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


class DoublySortedPointValues<T> {

    private final ArrayList<PointValuePair<T>> mPointValuesByX;
    private final ArrayList<PointValuePair<T>> mPointValuesByY;

    DoublySortedPointValues() {
        mPointValuesByX = new ArrayList<>();
        mPointValuesByY = new ArrayList<>();
    }

    int size() {
        return mPointValuesByX.size();
    }

    PointValuePair<T> getSingle() {
        if(BuildConfig.DEBUG && mPointValuesByX.size() != 1) {
            throw new AssertionError();
        }
        return mPointValuesByX.get(0);
    }

    Collection<PointValuePair<T>> getAll() {
        return mPointValuesByX;
    }

    void set(Collection<PointValuePair<T>> pointValues) {
        mPointValuesByX.clear();
        mPointValuesByX.addAll(pointValues);
        mPointValuesByY.clear();
        mPointValuesByY.addAll(pointValues);
    }

    void sort() {
        Collections.sort(mPointValuesByX, PointValuePair.compareXY);
        Collections.sort(mPointValuesByY, PointValuePair.compareYX);
    }

    private void splitOnX(DoublySortedPointValues<T> lesser, DoublySortedPointValues<T> greater, float splitX) {
        lesser.mPointValuesByX.clear();
        greater.mPointValuesByX.clear();
        for(PointValuePair<T> pvp : mPointValuesByX) {
            if(pvp.point.x <= splitX) {
                lesser.mPointValuesByX.add(pvp);
            } else {
                greater.mPointValuesByX.add(pvp);
            }
        }

        lesser.mPointValuesByY.clear();
        greater.mPointValuesByY.clear();
        for(PointValuePair<T> pvp : mPointValuesByY) {
            if(pvp.point.x <= splitX) {
                lesser.mPointValuesByY.add(pvp);
            } else {
                greater.mPointValuesByY.add(pvp);
            }
        }
    }

    float splitOnX(DoublySortedPointValues<T> lesser, DoublySortedPointValues<T> greater) {
        if(BuildConfig.DEBUG) {
            if(mPointValuesByX.size() < 2) {
                throw new AssertionError();
            }
        }

        float medianX = mPointValuesByX.get(mPointValuesByX.size() / 2 - 1).point.x;
        splitOnX(lesser, greater, medianX);

        if(greater.size() == 0) {
            medianX = (mPointValuesByX.get(0).point.x + mPointValuesByX.get(mPointValuesByX.size() - 1).point.x) / 2;
            splitOnX(lesser, greater, medianX);
        }

        return medianX;
    }

    private void splitOnY(DoublySortedPointValues<T> lesser, DoublySortedPointValues<T> greater, float splitY) {
        lesser.mPointValuesByX.clear();
        greater.mPointValuesByX.clear();
        for(PointValuePair<T> pvp : mPointValuesByX) {
            if(pvp.point.y <= splitY) {
                lesser.mPointValuesByX.add(pvp);
            } else {
                greater.mPointValuesByX.add(pvp);
            }
        }

        lesser.mPointValuesByY.clear();
        greater.mPointValuesByY.clear();
        for(PointValuePair<T> pvp : mPointValuesByY) {
            if(pvp.point.y <= splitY) {
                lesser.mPointValuesByY.add(pvp);
            } else {
                greater.mPointValuesByY.add(pvp);
            }
        }
    }

    float splitOnY(DoublySortedPointValues<T> lesser, DoublySortedPointValues<T> greater) {
        if(BuildConfig.DEBUG) {
            if(mPointValuesByY.size() < 2) {
                throw new AssertionError();
            }
        }

        float medianY = mPointValuesByY.get(mPointValuesByY.size() / 2 - 1).point.y;
        splitOnY(lesser, greater, medianY);

        if(greater.size() == 0) {
            medianY = (mPointValuesByY.get(0).point.y + mPointValuesByY.get(mPointValuesByY.size() - 1).point.y) / 2;
            splitOnY(lesser, greater, medianY);
        }

        return medianY;
    }
}
