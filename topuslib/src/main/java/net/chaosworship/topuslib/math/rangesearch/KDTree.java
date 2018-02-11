package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KDTree<T> implements RectangularSearch<T> {

    //////////////////////////////////////////////////

    private interface Node<T> {

        // create (sub)tree
        void insert(DoublySortedPointValues<T> pointValues);

        // add all values with points in area to the given collection
        void search(Rectangle area, Collection<T> searchResults);
    }

    //////////////////////////////////////////////////

    private static class DegenerateNode<T> implements Node<T> {

        private ArrayList<PointValuePair<T>> mPointValues;

        DegenerateNode() {
            mPointValues = new ArrayList<>();
        }

        @Override
        public void insert(DoublySortedPointValues<T> pointValues) {
            mPointValues.addAll(pointValues.getAll());
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {
            for(PointValuePair<T> pvp : mPointValues) {
                if(area.containsClosed(pvp.point)) {
                    searchResults.add(pvp.value);
                }
            }
        }
    }

    //////////////////////////////////////////////////

    private static class EvenNode<T> implements Node<T> {

        private Node<T> mLesserXChild;
        private Node<T> mGreaterXChild;
        private float mMedianX;

        EvenNode() {
            mLesserXChild = null;
            mGreaterXChild = null;
            mMedianX = 0;
        }

        @Override
        public void insert(DoublySortedPointValues<T> pointValues) {
            DoublySortedPointValues<T> lesser = new DoublySortedPointValues<>();
            DoublySortedPointValues<T> greater = new DoublySortedPointValues<>();
            mMedianX = pointValues.splitOnX(lesser, greater);
            if(lesser.size() == 0) {
                throw new AssertionError();
            }

            if(greater.size() == 0) {
                mGreaterXChild = new DegenerateNode<>();
                mLesserXChild = new DegenerateNode<>();
                mLesserXChild.insert(lesser);
            } else {
                if(lesser.size() > 1) {
                    mLesserXChild = new OddNode<>();
                } else {
                    mLesserXChild = new LeafNode<>();
                }
                mLesserXChild.insert(lesser);

                if(greater.size() > 1) {
                    mGreaterXChild = new OddNode<>();
                } else {
                    mGreaterXChild = new LeafNode<>();
                }
                mGreaterXChild.insert(greater);
            }
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {
            if(area.minx <= mMedianX) {
                mLesserXChild.search(area, searchResults);
            }
            if(area.maxx > mMedianX) {
                mGreaterXChild.search(area, searchResults);
            }
        }
    }

    //////////////////////////////////////////////////

    private static class OddNode<T> implements Node<T> {

        private Node<T> mLesserYChild;
        private Node<T> mGreaterYChild;
        private float mMedianY;

        OddNode() {
            mLesserYChild = null;
            mGreaterYChild = null;
            mMedianY = 0;
        }

        @Override
        public void insert(DoublySortedPointValues<T> pointValues) {
            DoublySortedPointValues<T> lesser = new DoublySortedPointValues<>();
            DoublySortedPointValues<T> greater = new DoublySortedPointValues<>();
            mMedianY = pointValues.splitOnY(lesser, greater);
            if(lesser.size() == 0) {
                throw new AssertionError();
            }

            if(greater.size() == 0) {
                mGreaterYChild = new DegenerateNode<>();
                mLesserYChild = new DegenerateNode<>();
                mLesserYChild.insert(lesser);
            } else {
                if(lesser.size() > 1) {
                    mLesserYChild = new EvenNode<>();
                } else {
                    mLesserYChild = new LeafNode<>();
                }
                mLesserYChild.insert(lesser);

                if(greater.size() > 1) {
                    mGreaterYChild = new EvenNode<>();
                } else {
                    mGreaterYChild = new LeafNode<>();
                }
                mGreaterYChild.insert(greater);
            }
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {
            if(area.miny <= mMedianY) {
                mLesserYChild.search(area, searchResults);
            }
            if(area.maxy > mMedianY) {
                mGreaterYChild.search(area, searchResults);
            }
        }
    }

    //////////////////////////////////////////////////

    private static class LeafNode<T> implements Node<T> {

        private PointValuePair<T> mPointValue;

        LeafNode() {
            mPointValue = null;
        }

        @Override
        public void insert(DoublySortedPointValues<T> pointValues) {
            mPointValue = pointValues.getSingle();
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {
            if(area.containsClosed(mPointValue.point)) {
                searchResults.add(mPointValue.value);
            }
        }
    }

    //////////////////////////////////////////////////

    private Node<T> mRoot;
    private final ArrayList<T> mSearchResults;
    private final DoublySortedPointValues<T> mPointValues;

    public KDTree() {
        mRoot = null;
        mSearchResults = new ArrayList<>();
        mPointValues = new DoublySortedPointValues<>();
    }

    @Override
    public void load(Collection<PointValuePair<T>> pointValues) {
        mPointValues.set(pointValues);
        reload();
    }

    // if the collection of points/values objects hasn't changed but the points themselves have been altered
    public void reload() {
        mPointValues.sort();
        mRoot = null;
        if(mPointValues.size() > 0) {
            if(mPointValues.size() == 1) {
                mRoot = new LeafNode<>();
            } else {
                mRoot = new EvenNode<>();
            }
            mRoot.insert(mPointValues);
        }
    }

    @Override
    public List<T> search(Rectangle area) {
        mSearchResults.clear();
        if(mRoot != null) {
            mRoot.search(area, mSearchResults);
        }
        return mSearchResults;
    }
}
