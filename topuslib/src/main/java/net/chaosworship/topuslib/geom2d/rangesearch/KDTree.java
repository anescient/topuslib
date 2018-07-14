package net.chaosworship.topuslib.geom2d.rangesearch;

import net.chaosworship.topuslib.geom2d.ClippedRectangle;
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
        ClippedRectangle mRegion;
        OddNode<T> mLesserXChild;
        OddNode<T> mGreaterXChild;

        EvenNode() {
            mPointObjects = new DoublySortedPointObjects();
            mLeafPointObject = null;
            mMedianX = 0;
            mRegion = new ClippedRectangle();
            mLesserXChild = null;
            mGreaterXChild = null;
        }

        void set(Collection<PointObjectPair> pointObjects) {
            mPointObjects.set(pointObjects);
            rootUpdate();
        }

        void rootUpdate() {
            update(null, 0, false);
        }

        void update(ClippedRectangle parentRegion, float parentSplit, boolean greaterChild) {
            mLeafPointObject = null;
            if(mPointObjects.isEmpty()) {
                return;
            }
            if(mPointObjects.size() == 1) {
                mLeafPointObject = mPointObjects.getSingle();
                return;
            }
            if(mLesserXChild == null) {
                if(mGreaterXChild != null) throw new AssertionError();
                mLesserXChild = new OddNode<>();
                mGreaterXChild = new OddNode<>();
            }
            mMedianX = mPointObjects.splitOnX(mLesserXChild.mPointObjects, mGreaterXChild.mPointObjects);
            if(mLesserXChild.mPointObjects.isEmpty() && mGreaterXChild.mPointObjects.isEmpty()) {
                throw new AssertionError();
            }

            if(parentRegion == null) {
                mRegion.setUnbounded();
            } else {
                mRegion.set(parentRegion);
                if(greaterChild) {
                    mRegion.clipMinY(parentSplit);
                } else {
                    mRegion.clipMaxY(parentSplit);
                }
            }

            if(!mGreaterXChild.mPointObjects.isEmpty()) {
                mLesserXChild.update(mRegion, mMedianX, false);
                mGreaterXChild.update(mRegion, mMedianX, true);
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mPointObjects.isEmpty()) {
                return;
            }
            if(mLeafPointObject != null) {
                if(area.containsClosed(mLeafPointObject.point)) {
                    //noinspection unchecked
                    searchResults.add((T)mLeafPointObject.value);
                }
            } else if(mGreaterXChild.mPointObjects.isEmpty()) {
                mPointObjects.getValuesInRect(area, searchResults);
            } else {
                if(mRegion.isContainedBy(area)) {
                    mPointObjects.getAllValues(searchResults);
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
    }

    //////////////////////////////////////////////////////////

    private static class OddNode<T> {
        DoublySortedPointObjects mPointObjects;
        PointObjectPair mLeafPointObject;
        float mMedianY;
        ClippedRectangle mRegion;
        EvenNode<T> mLesserYChild;
        EvenNode<T> mGreaterYChild;

        OddNode() {
            mPointObjects = new DoublySortedPointObjects();
            mLeafPointObject = null;
            mMedianY = 0;
            mRegion = new ClippedRectangle();
            mLesserYChild = null;
            mGreaterYChild = null;
        }

        void update(ClippedRectangle parentRegion, float parentSplit, boolean greaterChild) {
            if(parentRegion == null) {
                throw new IllegalArgumentException();
            }
            mLeafPointObject = null;
            if(mPointObjects.isEmpty()) {
                return;
            }
            if(mPointObjects.size() == 1) {
                mLeafPointObject = mPointObjects.getSingle();
                return;
            }
            if(mLesserYChild == null) {
                if(mGreaterYChild != null) throw new AssertionError();
                mLesserYChild = new EvenNode<>();
                mGreaterYChild = new EvenNode<>();
            }
            mMedianY = mPointObjects.splitOnY(mLesserYChild.mPointObjects, mGreaterYChild.mPointObjects);
            if(mLesserYChild.mPointObjects.isEmpty() && mGreaterYChild.mPointObjects.isEmpty()) {
                throw new AssertionError();
            }

            mRegion.set(parentRegion);
            if(greaterChild) {
                mRegion.clipMinX(parentSplit);
            } else {
                mRegion.clipMaxX(parentSplit);
            }

            if(!mGreaterYChild.mPointObjects.isEmpty()) {
                mLesserYChild.update(mRegion, mMedianY, false);
                mGreaterYChild.update(mRegion, mMedianY, true);
            }
        }

        void search(Rectangle area, Collection<T> searchResults) {
            if(mPointObjects.isEmpty()) {
                return;
            }
            if(mLeafPointObject != null) {
                if(area.containsClosed(mLeafPointObject.point)) {
                    //noinspection unchecked
                    searchResults.add((T)mLeafPointObject.value);
                }
            } else if(mGreaterYChild.mPointObjects.isEmpty()) {
                mPointObjects.getValuesInRect(area, searchResults);
            } else {
                if(mRegion.isContainedBy(area)) {
                    mPointObjects.getAllValues(searchResults);
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
        mRoot.rootUpdate();
    }

    public List<T> search(Rectangle area) {
        mSearchResults.clear();
        if(mRoot != null) {
            //noinspection unchecked
            mRoot.search(area, mSearchResults);
        }
        return mSearchResults;
    }
}
