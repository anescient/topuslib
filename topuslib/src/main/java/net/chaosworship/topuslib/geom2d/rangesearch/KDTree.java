package net.chaosworship.topuslib.geom2d.rangesearch;

import net.chaosworship.topuslib.tuple.PointObjectPair;
import net.chaosworship.topuslib.tuple.PointValuePair;
import net.chaosworship.topuslib.geom2d.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KDTree<T> {

    //////////////////////////////////////////////////////////

    private static class EvenNode<T> {
        DoublySortedPointObjects mPointObjects;
        PointObjectPair mLeafPointObject;
        float mMedianX;
        OddNode<T> mLesserXChild;
        OddNode<T> mGreaterXChild;
        boolean mIsDegenerate;

        EvenNode() {
            mPointObjects = new DoublySortedPointObjects();
            mLeafPointObject = null;
            mMedianX = 0;
            mLesserXChild = null;
            mGreaterXChild = null;
            mIsDegenerate = true;
        }

        void set(Collection<PointObjectPair> pointObjects) {
            mPointObjects.set(pointObjects);
            update();
        }

        void update() {
            mLeafPointObject = null;
            mIsDegenerate = false;
            if(mPointObjects.size() == 0) {
                mIsDegenerate = true;
            } else if(mPointObjects.size() == 1) {
                mLeafPointObject = mPointObjects.getSingle();
            } else {
                if(mLesserXChild == null) {
                    if(mGreaterXChild != null) throw new AssertionError();
                    mLesserXChild = new OddNode<>();
                    mGreaterXChild = new OddNode<>();
                }
                mMedianX = mPointObjects.splitOnX(mLesserXChild.pointObjects, mGreaterXChild.pointObjects);
                if(mGreaterXChild.pointObjects.size() == 0) {
                    mIsDegenerate = true;
                } else {
                    mLesserXChild.update();
                    mGreaterXChild.update();
                }
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointObject != null) {
                if(area.containsClosed(mLeafPointObject.point)) {
                    searchResults.add((T) mLeafPointObject.value);
                }
            } else if(mIsDegenerate) {
                ArrayList<PointObjectPair> pops = new ArrayList<>();
                mPointObjects.getAll(pops);
                for(PointObjectPair pop : pops) {
                    if(area.containsClosed(pop.point)) {
                        searchResults.add((T)pop.value);
                    }
                }
            } else {
                if(area.minx <= mMedianX) {
                    mLesserXChild.search(area, searchResults);
                }
                if(area.maxx > mMedianX) {
                    mGreaterXChild.search(area, searchResults);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////

    private static class OddNode<T> {
        DoublySortedPointObjects pointObjects;
        PointObjectPair mLeafPointPointObject;
        float mMedianY;
        EvenNode<T> mLesserYChild;
        EvenNode<T> mGreaterYChild;
        boolean mIsDegenerate;

        OddNode() {
            pointObjects = new DoublySortedPointObjects();
            mLeafPointPointObject = null;
            mMedianY = 0;
            mLesserYChild = null;
            mGreaterYChild = null;
            mIsDegenerate = true;
        }

        void update() {
            mLeafPointPointObject = null;
            mIsDegenerate = false;
            if(pointObjects.size() == 0) {
                mIsDegenerate = true;
            } else if(pointObjects.size() == 1) {
                mLeafPointPointObject = pointObjects.getSingle();
            } else {
                if(mLesserYChild == null) {
                    if(mGreaterYChild != null) throw new AssertionError();
                    mLesserYChild = new EvenNode<>();
                    mGreaterYChild = new EvenNode<>();
                }
                mMedianY = pointObjects.splitOnY(mLesserYChild.mPointObjects, mGreaterYChild.mPointObjects);
                if(mGreaterYChild.mPointObjects.size() == 0) {
                    mIsDegenerate = true;
                } else {
                    mLesserYChild.update();
                    mGreaterYChild.update();
                }
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointPointObject != null) {
                if(area.containsClosed(mLeafPointPointObject.point)) {
                    searchResults.add((T) mLeafPointPointObject.value);
                }
            } else if(mIsDegenerate) {
                ArrayList<PointObjectPair> pops = new ArrayList<>();
                pointObjects.getAll(pops);
                for(PointObjectPair pop : pops) {
                    if(area.containsClosed(pop.point)) {
                        searchResults.add((T)pop.value);
                    }
                }
            } else {
                if(area.miny <= mMedianY) {
                    mLesserYChild.search(area, searchResults);
                }
                if(area.maxy > mMedianY) {
                    mGreaterYChild.search(area, searchResults);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////

    private EvenNode mRoot;
    private final ArrayList<T> mSearchResults;

    public KDTree() {
        mRoot = new EvenNode<>();
        mSearchResults = new ArrayList<>();
    }

    // clear and load
    public void load(List<PointValuePair<T>> pointValues) {
        ArrayList<PointObjectPair> pops = new ArrayList<>(pointValues.size());
        for(PointValuePair<T> pvp : pointValues) {
            pops.add(new PointObjectPair(pvp.point, pvp.value));
        }
        mRoot.set(pops);
    }

    // if the collection of points/values objects hasn't changed but the points themselves have been altered
    public void reload() {
        mRoot.mPointObjects.sort();
        mRoot.update();
    }

    public List<T> search(Rectangle area) {
        mSearchResults.clear();
        if(mRoot != null) {
            mRoot.search(area, mSearchResults);
        }
        return mSearchResults;
    }
}
