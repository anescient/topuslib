package net.chaosworship.topuslib.geom2d;


// this rectangular bound can be infinite, infinite half-plane, etc.
// but it can only be reduced in size, never expanded
public class ClippedRectangle {

    private Float mMinX;
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
            mMinX = x;
        }
    }

    public void clipMaxX(float x) {
        if(mMaxX == null || x < mMaxX) {
            mMaxX = x;
        }
    }

    public void clipMinY(float y) {
        if(mMinY == null || y > mMinY) {
            mMinY = y;
        }
    }

    public void clipMaxY(float y) {
        if(mMaxY == null || y < mMaxY) {
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

    public boolean overlapsRect(Rectangle rect) {
        return
            (mMinX == null || mMinX < rect.maxx) &&
            (mMaxX == null || mMaxX > rect.minx) &&
            (mMaxY == null || mMaxY > rect.miny) &&
            (mMinY == null || mMinY < rect.maxy);
    }
}
