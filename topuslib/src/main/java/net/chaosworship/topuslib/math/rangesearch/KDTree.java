package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;


public class KDTree<T> implements RectangularSearch<T> {

    //////////////////////////////////////////////////

    private static class KDTreePool<T> {
        private final ArrayList<LeafNode<T>> mLeafNodes;
        private int mLeafNodesUsed;
        private final ArrayList<DegenerateNode<T>> mDegenerateNodes;
        private int mDegenerateNodesUsed;
        private final ArrayList<EvenNode<T>> mEvenNodes;
        private int mEvenNodesUsed;
        private final ArrayList<OddNode<T>> mOddNodes;
        private int mOddNodesUsed;
        private final Stack<DoublySortedPointValues<T>> mSortedPointValues;

        KDTreePool() {
            mLeafNodes = new ArrayList<>();
            mDegenerateNodes = new ArrayList<>();
            mEvenNodes = new ArrayList<>();
            mOddNodes = new ArrayList<>();
            mSortedPointValues = new Stack<>();
            reset();
        }

        void reset() {
            mLeafNodesUsed = 0;
            mDegenerateNodesUsed = 0;
            mEvenNodesUsed = 0;
            mOddNodesUsed = 0;
        }

        LeafNode<T> getLeafNode() {
            if(mLeafNodesUsed == mLeafNodes.size()) {
                mLeafNodes.add(new LeafNode<T>());
            }
            return mLeafNodes.get(mLeafNodesUsed++);
        }

        DegenerateNode<T> getDegenerateNode() {
            if(mDegenerateNodesUsed == mDegenerateNodes.size()) {
                mDegenerateNodes.add(new DegenerateNode<T>());
            }
            return mDegenerateNodes.get(mDegenerateNodesUsed++);
        }

        EvenNode<T> getEvenNode() {
            if(mEvenNodesUsed == mEvenNodes.size()) {
                mEvenNodes.add(new EvenNode<>(this));
            }
            return mEvenNodes.get(mEvenNodesUsed++);
        }

        OddNode<T> getOddNode() {
            if(mOddNodesUsed == mOddNodes.size()) {
                mOddNodes.add(new OddNode<>(this));
            }
            return mOddNodes.get(mOddNodesUsed++);
        }

        DoublySortedPointValues<T> getSortedPointValues() {
            if(mSortedPointValues.empty()) {
                return new DoublySortedPointValues<>();
            } else {
                return mSortedPointValues.pop();
            }
        }

        void putSortedPointValues(DoublySortedPointValues<T> sortedPointValues) {
            mSortedPointValues.push(sortedPointValues);
        }
    }

    //////////////////////////////////////////////////

    private interface Node<T> {

        // create (sub)tree
        void set(DoublySortedPointValues<T> pointValues);

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
        public void set(DoublySortedPointValues<T> pointValues) {
            mPointValues.clear();
            if(pointValues != null) {
                mPointValues.addAll(pointValues.getAll());
            }
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

        private final KDTreePool<T> mPool;
        private Node<T> mLesserXChild;
        private Node<T> mGreaterXChild;
        private float mMedianX;

        EvenNode(KDTreePool<T> pool) {
            mPool = pool;
            mLesserXChild = null;
            mGreaterXChild = null;
            mMedianX = 0;
        }

        @Override
        public void set(DoublySortedPointValues<T> pointValues) {
            DoublySortedPointValues<T> lesser = mPool.getSortedPointValues();
            DoublySortedPointValues<T> greater = mPool.getSortedPointValues();
            mMedianX = pointValues.splitOnX(lesser, greater);
            if(lesser.size() == 0) {
                throw new AssertionError();
            }

            if(greater.size() == 0) {
                mGreaterXChild = mPool.getDegenerateNode();
                mGreaterXChild.set(null);
                mPool.putSortedPointValues(greater);
                mLesserXChild = mPool.getDegenerateNode();
                mLesserXChild.set(lesser);
                mPool.putSortedPointValues(lesser);
            } else {
                if(lesser.size() > 1) {
                    mLesserXChild = mPool.getOddNode();
                } else {
                    mLesserXChild = mPool.getLeafNode();
                }
                mLesserXChild.set(lesser);
                mPool.putSortedPointValues(lesser);

                if(greater.size() > 1) {
                    mGreaterXChild = mPool.getOddNode();
                } else {
                    mGreaterXChild = mPool.getLeafNode();
                }
                mGreaterXChild.set(greater);
                mPool.putSortedPointValues(greater);
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

        private final KDTreePool<T> mPool;
        private Node<T> mLesserYChild;
        private Node<T> mGreaterYChild;
        private float mMedianY;

        OddNode(KDTreePool<T> pool) {
            mPool = pool;
            mLesserYChild = null;
            mGreaterYChild = null;
            mMedianY = 0;
        }

        @Override
        public void set(DoublySortedPointValues<T> pointValues) {
            DoublySortedPointValues<T> lesser = mPool.getSortedPointValues();
            DoublySortedPointValues<T> greater = mPool.getSortedPointValues();
            mMedianY = pointValues.splitOnY(lesser, greater);
            if(lesser.size() == 0) {
                throw new AssertionError();
            }

            if(greater.size() == 0) {
                mGreaterYChild = mPool.getDegenerateNode();
                mGreaterYChild.set(null);
                mPool.putSortedPointValues(greater);
                mLesserYChild = mPool.getDegenerateNode();
                mLesserYChild.set(lesser);
                mPool.putSortedPointValues(lesser);
            } else {
                if(lesser.size() > 1) {
                    mLesserYChild = mPool.getEvenNode();
                } else {
                    mLesserYChild = mPool.getLeafNode();
                }
                mLesserYChild.set(lesser);
                mPool.putSortedPointValues(lesser);

                if(greater.size() > 1) {
                    mGreaterYChild = mPool.getEvenNode();
                } else {
                    mGreaterYChild = mPool.getLeafNode();
                }
                mGreaterYChild.set(greater);
                mPool.putSortedPointValues(greater);
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
        public void set(DoublySortedPointValues<T> pointValues) {
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
    private final KDTreePool<T> mPool;

    public KDTree() {
        mRoot = null;
        mSearchResults = new ArrayList<>();
        mPointValues = new DoublySortedPointValues<>();
        mPool = new KDTreePool<>();
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
        mPool.reset();
        if(mPointValues.size() > 0) {
            if(mPointValues.size() == 1) {
                mRoot = mPool.getLeafNode();
            } else {
                mRoot = mPool.getEvenNode();
            }
            mRoot.set(mPointValues);
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
