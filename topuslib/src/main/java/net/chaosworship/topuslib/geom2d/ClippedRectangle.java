package net.chaosworship.topuslib.geom2d;


import net.chaosworship.topuslib.BuildConfig;

// this rectangular bound can be infinite, infinite half-plane, etc.
// but it can only be reduced in size, never expanded
public class ClippedRectangle {

    private Float mMinX; // null is infinity for these values
    private Float mMaxX;
    private Float mMinY;
    private Float mMaxY;

    public ClippedRectangle() {
        setUnbounded();
    }

    public ClippedRectangle(ClippedRectangle source) {
        mMinX = source.mMinX;
        mMaxX = source.mMaxX;
        mMinY = source.mMinY;
        mMaxY = source.mMaxY;
    }

    public void set(ClippedRectangle source) {
        mMinX = source.mMinX;
        mMaxX = source.mMaxX;
        mMinY = source.mMinY;
        mMaxY = source.mMaxY;
    }

    public void setUnbounded() {
        mMinX = null;
        mMaxX = null;
        mMinY = null;
        mMaxY = null;
    }

    public Rectangle asRectangle() {
        if(mMinX == null || mMaxX == null || mMinY == null || mMaxY == null) {
            throw new IllegalStateException();
        }
        return new Rectangle(mMinX, mMinY, mMaxX, mMaxY);
    }

    public void clipMinX(float x) {
        if(mMinX == null || x > mMinX) {
            if(mMaxX != null && x > mMaxX) {
                x = mMaxX;
            }
            mMinX = x;
        }
    }

    public void clipMaxX(float x) {
        if(mMaxX == null || x < mMaxX) {
            if(mMinX != null && x < mMinX) {
                x = mMinX;
            }
            mMaxX = x;
        }
    }

    public void clipMinY(float y) {
        if(mMinY == null || y > mMinY) {
            if(mMaxY != null && y > mMaxY) {
                y = mMaxY;
            }
            mMinY = y;
        }
    }

    public void clipMaxY(float y) {
        if(mMaxY == null || y < mMaxY) {
            if(mMinY != null && y < mMinY) {
                y = mMinY;
            }
            mMaxY = y;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean containsOpen(float x, float y) {
        return
            (mMinX == null || mMinX < x) &&
            (mMaxX == null || mMaxX > x) &&
            (mMaxY == null || mMaxY > y) &&
            (mMinY == null || mMinY < y);
    }

    public boolean containsOpen(Vec2 v) {
        return containsOpen(v.x, v.y);
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean containsOpen(Rectangle rect) {
        return
            (mMinX == null || rect.minx > mMinX) &&
            (mMaxX == null || rect.maxx < mMaxX) &&
            (mMinY == null || rect.miny > mMinY) &&
            (mMaxY == null || rect.maxy < mMaxY);
    }

    public boolean isContainedBy(Rectangle rect) {
        //noinspection SimplifiableIfStatement
        if(mMinX == null || mMaxX == null || mMinY == null || mMaxY == null) {
            return false;
        }
        if(BuildConfig.DEBUG) {
            if(rect.minx > rect.maxx || rect.miny > rect.maxy)
                throw new AssertionError();
            if(mMinX > mMaxX || mMinY > mMaxY)
                throw new AssertionError();
        }
        return mMinX >= rect.minx && mMaxX <= rect.maxx && mMinY >= rect.miny && mMaxY <= rect.maxy;
    }

    public boolean overlapsRect(Rectangle rect) {
        return
            (mMinX == null || mMinX < rect.maxx) &&
            (mMaxX == null || mMaxX > rect.minx) &&
            (mMaxY == null || mMaxY > rect.miny) &&
            (mMinY == null || mMinY < rect.maxy);
    }
}
