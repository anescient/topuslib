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
        DoublySortedPointObjects mPointValues;
        PointObjectPair mLeafPointValue;
        float mMedianX;
        OddNode<T> mLesserXChild;
        OddNode<T> mGreaterXChild;
        boolean mIsDegenerate;

        EvenNode() {
            mPointValues = new DoublySortedPointObjects();
            mLeafPointValue = null;
            mMedianX = 0;
            mLesserXChild = null;
            mGreaterXChild = null;
            mIsDegenerate = true;
        }

        void set(Collection<PointObjectPair> pointValues) {
            mPointValues.set(pointValues);
            update();
        }

        void update() {
            mLeafPointValue = null;
            mIsDegenerate = false;
            if(mPointValues.size() == 0) {
                mIsDegenerate = true;
            } else if(mPointValues.size() == 1) {
                mLeafPointValue = mPointValues.getSingle();
            } else {
                if(mLesserXChild == null) {
                    if(mGreaterXChild != null) throw new AssertionError();
                    mLesserXChild = new OddNode<>();
                    mGreaterXChild = new OddNode<>();
                }
                mMedianX = mPointValues.splitOnX(mLesserXChild.mPointValues, mGreaterXChild.mPointValues);
                if(mGreaterXChild.mPointValues.size() == 0) {
                    mIsDegenerate = true;
                } else {
                    mLesserXChild.update();
                    mGreaterXChild.update();
                }
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointValue != null) {
                if(area.containsClosed(mLeafPointValue.point)) {
                    searchResults.add((T)mLeafPointValue.value);
                }
            } else if(mIsDegenerate) {
                ArrayList<PointObjectPair> pops = new ArrayList<>();
                mPointValues.getAll(pops);
                for(PointObjectPair pvp : pops) {
                    if(area.containsClosed(pvp.point)) {
                        searchResults.add((T)pvp.value);
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
        DoublySortedPointObjects mPointValues;
        PointObjectPair mLeafPointValue;
        float mMedianY;
        EvenNode<T> mLesserYChild;
        EvenNode<T> mGreaterYChild;
        boolean mIsDegenerate;

        OddNode() {
            mPointValues = new DoublySortedPointObjects();
            mLeafPointValue = null;
            mMedianY = 0;
            mLesserYChild = null;
            mGreaterYChild = null;
            mIsDegenerate = true;
        }

        void update() {
            mLeafPointValue = null;
            mIsDegenerate = false;
            if(mPointValues.size() == 0) {
                mIsDegenerate = true;
            } else if(mPointValues.size() == 1) {
                mLeafPointValue = mPointValues.getSingle();
            } else {
                if(mLesserYChild == null) {
                    if(mGreaterYChild != null) throw new AssertionError();
                    mLesserYChild = new EvenNode<>();
                    mGreaterYChild = new EvenNode<>();
                }
                mMedianY = mPointValues.splitOnY(mLesserYChild.mPointValues, mGreaterYChild.mPointValues);
                if(mGreaterYChild.mPointValues.size() == 0) {
                    mIsDegenerate = true;
                } else {
                    mLesserYChild.update();
                    mGreaterYChild.update();
                }
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointValue != null) {
                if(area.containsClosed(mLeafPointValue.point)) {
                    searchResults.add((T)mLeafPointValue.value);
                }
            } else if(mIsDegenerate) {
                ArrayList<PointObjectPair> pops = new ArrayList<>();
                mPointValues.getAll(pops);
                for(PointObjectPair pvp : pops) {
                    if(area.containsClosed(pvp.point)) {
                        searchResults.add((T)pvp.value);
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
        mRoot.mPointValues.sort();
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
