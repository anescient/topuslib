package net.chaosworship.topuslib.geom2d.rangesearch;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointValuePair;

import java.util.ArrayList;
import java.util.Collection;


// rectangular search supporting incremental point insertion
// balance is not guaranteed
// bound of (future) points needs to be provided
// best to avoid large result sets
// identical points are not supported, new value will simply replace old value
public class FixedKDTree<T> {

    //////////////////////////////////////////////////////////

    private static class EvenNode<T> {
        private PointValuePair<T> mLeafPointValue; // null for internal nodes
        private final Rectangle mArea;
        private final float mSplitX;
        private OddNode<T> mLesserXChild;
        private OddNode<T> mGreaterXChild;

        private EvenNode(Rectangle area) {
            mLeafPointValue = null;
            mArea = area;
            mSplitX = area.centerX();
            mLesserXChild = null;
            mGreaterXChild = null;
        }

        private void insert(PointValuePair<T> pointValuePair) {
            if(mLeafPointValue != null && mLeafPointValue.point.equals(pointValuePair.point)) {
                mLeafPointValue = pointValuePair;
                return;
            }
            if(mLeafPointValue == null && mLesserXChild == null) {
                mLeafPointValue = pointValuePair;
            } else {
                if(mLesserXChild == null) {
                    Rectangle lesserHalf = new Rectangle(mArea.minx, mArea.miny, mSplitX, mArea.maxy);
                    mLesserXChild = new OddNode<>(lesserHalf);
                    Rectangle greaterHalf = new Rectangle(mSplitX, mArea.miny, mArea.maxx, mArea.maxy);
                    mGreaterXChild = new OddNode<>(greaterHalf);
                }

                if(pointValuePair.point.x < mSplitX) {
                    mLesserXChild.insert(pointValuePair);
                } else {
                    mGreaterXChild.insert(pointValuePair);
                }

                if(mLeafPointValue != null) {
                    if(mLeafPointValue.point.x < mSplitX) {
                        mLesserXChild.insert(mLeafPointValue);
                    } else {
                        mGreaterXChild.insert(mLeafPointValue);
                    }
                    mLeafPointValue = null;
                }
            }
        }

        private void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointValue != null) {
                if(area.contains(mLeafPointValue.point)) {
                    searchResults.add(mLeafPointValue.value);
                }
            } else if (mLesserXChild != null) {
                if(area.minx <= mSplitX) {
                    mLesserXChild.search(area, searchResults);
                }
                if(area.maxx >= mSplitX) {
                    mGreaterXChild.search(area, searchResults);
                }
            }
        }
    }

    //////////////////////////////////////////////////////////

    private static class OddNode<T> {
        private PointValuePair<T> mLeafPointValue; // null for internal nodes
        private final Rectangle mArea;
        private float mSplitY;
        private EvenNode<T> mLesserYChild;
        private EvenNode<T> mGreaterYChild;

        private OddNode(Rectangle area) {
            mLeafPointValue = null;
            mArea = area;
            mSplitY = mArea.centerY();
            mLesserYChild = null;
            mGreaterYChild = null;
        }

        private void insert(PointValuePair<T> pointValuePair) {
            if(mLeafPointValue != null && mLeafPointValue.point.equals(pointValuePair.point)) {
                mLeafPointValue = pointValuePair;
                return;
            }
            if(mLeafPointValue == null && mLesserYChild == null) {
                mLeafPointValue = pointValuePair;
            } else {
                if(mLesserYChild == null) {
                    Rectangle lesserHalf = new Rectangle(mArea.minx, mArea.miny, mArea.maxx, mSplitY);
                    mLesserYChild = new EvenNode<>(lesserHalf);
                    Rectangle greaterHalf = new Rectangle(mArea.minx, mSplitY, mArea.maxx, mArea.maxy);
                    mGreaterYChild = new EvenNode<>(greaterHalf);
                }

                if(pointValuePair.point.y < mSplitY) {
                    mLesserYChild.insert(pointValuePair);
                } else {
                    mGreaterYChild.insert(pointValuePair);
                }

                if(mLeafPointValue != null) {
                    if(mLeafPointValue.point.y < mSplitY) {
                        mLesserYChild.insert(mLeafPointValue);
                    } else {
                        mGreaterYChild.insert(mLeafPointValue);
                    }
                    mLeafPointValue = null;
                }
            }
        }

        private void search(Rectangle area, Collection<T> searchResults) {
            if(mLeafPointValue != null) {
                if(area.contains(mLeafPointValue.point)) {
                    searchResults.add(mLeafPointValue.value);
                }
            } else if (mLesserYChild != null) {
                if(area.miny <= mSplitY) {
                    mLesserYChild.search(area, searchResults);
                }
                if(area.maxy >= mSplitY) {
                    mGreaterYChild.search(area, searchResults);
                }
            }
        }

    }

    //////////////////////////////////////////////////////////

    // area the tree will cover
    private final Rectangle mArea;

    // points that fall outside the tree search area
    private final ArrayList<PointValuePair<T>> mOuterPoints;

    private final EvenNode<T> mRoot;

    private final ArrayList<T> mSearchResults;

    public FixedKDTree(Rectangle area) {
        mArea = new Rectangle(area);
        mOuterPoints = new ArrayList<>();
        mRoot = new EvenNode<>(area);
        mSearchResults = new ArrayList<>();
    }

    public void insert(PointValuePair<T> pointValuePair) {
        if(mArea.contains(pointValuePair.point)) {
            mRoot.insert(pointValuePair);
        } else {
            mOuterPoints.add(pointValuePair);
        }
    }

    public void insert(Vec2 point, T value) {
        insert(new PointValuePair<>(point, value));
    }

    public ArrayList<T> search(Rectangle area) {
        mSearchResults.clear();
        mRoot.search(area, mSearchResults);
        for(PointValuePair<T> pvp : mOuterPoints) {
            if(area.contains(pvp.point)) {
                mSearchResults.add(pvp.value);
            }
        }
        return mSearchResults;
    }
}
