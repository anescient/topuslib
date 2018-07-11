package net.chaosworship.topuslib.geom2d.barneshut;

import android.util.Log;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;


class BarnesHutNode {

    private static final float OPENINGRATIO = 0.5f;

    private static int nodecount = 0;
    private static int maxdepth = 0;

    private final int mDepth;
    private final Rectangle mArea;
    private final float mSplitX;
    private final float mSplitY;
    private PointMass mSumPointMass;
    private int mPointCount;
    private BarnesHutNode mChildLessXLessY;
    private BarnesHutNode mChildLessXMoreY;
    private BarnesHutNode mChildMoreXLessY;
    private BarnesHutNode mChildMoreXMoreY;

    private final Vec2 mTempDiff;

    BarnesHutNode(Rectangle area) {
        this(area, 0);
    }

    private BarnesHutNode(Rectangle area, int depth) {
        nodecount++;
        if(nodecount % 1000 == 0)
            Log.d("bht nodes", String.valueOf(nodecount));

        mDepth = depth;
        if(mDepth > maxdepth) {
            maxdepth = mDepth;
            Log.d("bht depth", String.valueOf(maxdepth));
        }

        mArea = area;
        mSplitX = mArea.centerX();
        mSplitY = mArea.centerY();
        mSumPointMass = new PointMass();
        mPointCount = 0;
        mChildLessXLessY = null;
        mChildLessXMoreY = null;
        mChildMoreXLessY = null;
        mChildMoreXMoreY = null;
        mTempDiff = new Vec2();
    }

    float avgLeafDepth() {
        if(mPointCount <= 1)
            return mDepth;
        else {
            float s = 0;
            s += 0.25 * mChildLessXLessY.avgLeafDepth();
            s += 0.25 * mChildLessXMoreY.avgLeafDepth();
            s += 0.25 * mChildMoreXLessY.avgLeafDepth();
            s += 0.25 * mChildMoreXMoreY.avgLeafDepth();
            return s;
        }
    }

    int maxLeafDepth() {
        if(mPointCount <= 1)
            return mDepth;
        else {
            int d = 0;
            d = Math.max(d, mChildLessXLessY.maxLeafDepth());
            d = Math.max(d, mChildLessXMoreY.maxLeafDepth());
            d = Math.max(d, mChildMoreXLessY.maxLeafDepth());
            d = Math.max(d, mChildMoreXMoreY.maxLeafDepth());
            return d;
        }
    }

    int treeSize() {
        int n = 1;
        if(mChildLessXLessY != null)
            n += mChildLessXLessY.treeSize();
        if(mChildLessXMoreY != null)
            n += mChildLessXMoreY.treeSize();
        if(mChildMoreXLessY != null)
            n += mChildMoreXLessY.treeSize();
        if(mChildMoreXMoreY != null)
            n += mChildMoreXMoreY.treeSize();
        return n;
    }

    void clear() {
        if(mDepth > 7 && mPointCount == 0) {
            nodecount -= treeSize() - 1;
            mChildLessXLessY = null;
            mChildLessXMoreY = null;
            mChildMoreXLessY = null;
            mChildMoreXMoreY = null;
        }
        mPointCount = 0;
    }

    void insert(PointMass pointMass) {
        if(pointMass.mass < 0) {
            throw new IllegalArgumentException();
        }
        if(pointMass.mass == 0) {
            return;
        }

        if(mDepth == 0 && !mArea.contains(pointMass.position)) {
            return;
        }

        if(mPointCount == 0) {
            mSumPointMass.position.set(pointMass.position);
            mSumPointMass.mass = pointMass.mass;
        } else {
            if(mChildLessXLessY == null) {
                createChildren();
            }
            if(mPointCount == 1) {
                mChildLessXLessY.clear();
                mChildLessXMoreY.clear();
                mChildMoreXLessY.clear();
                mChildMoreXMoreY.clear();
                putToChild(mSumPointMass);
            }
            mSumPointMass.mass += pointMass.mass;
            mSumPointMass.position.addScaledDifference(
                    pointMass.position, mSumPointMass.position, pointMass.mass / mSumPointMass.mass);
            putToChild(pointMass);
        }

        mPointCount++;
    }

    private void putToChild(PointMass pointMass) {
        if(pointMass.position.x <= mSplitX) {
            if(pointMass.position.y <= mSplitY) {
                mChildLessXLessY.insert(pointMass);
            } else { // y > mSplitY
                mChildLessXMoreY.insert(pointMass);
            }
        } else { // x > mSplitX
            if(pointMass.position.y <= mSplitY) {
                mChildMoreXLessY.insert(pointMass);
            } else { // y > mSplitY
                mChildMoreXMoreY.insert(pointMass);
            }
        }
    }

    private void createChildren() {
        if(BuildConfig.DEBUG) {
            if(mChildLessXLessY != null ||
                    mChildLessXMoreY != null ||
                    mChildMoreXLessY != null ||
                    mChildMoreXMoreY != null) {
                throw new AssertionError();
            }
        }

        Rectangle childRect;

        childRect = new Rectangle(mArea.minx, mArea.miny, mSplitX, mSplitY);
        mChildLessXLessY = new BarnesHutNode(childRect, mDepth + 1);

        childRect = new Rectangle(mArea.minx, mSplitY, mSplitX, mArea.maxy);
        mChildLessXMoreY = new BarnesHutNode(childRect, mDepth + 1);

        childRect = new Rectangle(mSplitX, mArea.miny, mArea.maxx, mSplitY);
        mChildMoreXLessY = new BarnesHutNode(childRect, mDepth + 1);

        childRect = new Rectangle(mSplitX, mSplitY, mArea.maxx, mArea.maxy);
        mChildMoreXMoreY = new BarnesHutNode(childRect, mDepth + 1);
    }

    void getForce(Vec2 position, Vec2 forceAccum, float minDistance) {
        if(mPointCount == 0) {
            return;
        }

        boolean open = mArea.contains(position);
        if(!open) {
            float diameter = mArea.width() * 1.4142f; // diagonal of a square
            float distance = Vec2.distance(position, mSumPointMass.position);
            if(distance < OPENINGRATIO * diameter) {
                open = true;
            }
        }

        open &= mPointCount > 1;

        if(open) {
            mChildLessXLessY.getForce(position, forceAccum, minDistance);
            mChildLessXMoreY.getForce(position, forceAccum, minDistance);
            mChildMoreXLessY.getForce(position, forceAccum, minDistance);
            mChildMoreXMoreY.getForce(position, forceAccum, minDistance);
        } else {
            mTempDiff.setDifference(position, mSumPointMass.position);
            float distance = mTempDiff.magnitude();
            if(distance > 0) {
                mTempDiff.scaleInverse(distance);
                if(distance < minDistance) {
                    distance = minDistance;
                }
                forceAccum.addScaled(mTempDiff, mSumPointMass.mass / (distance * distance));
            }
        }
    }
}
