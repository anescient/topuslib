package net.chaosworship.topuslib.geom2d;


import java.util.Iterator;

@SuppressWarnings("UnusedReturnValue")
public class Rectangle {

    public float minx;
    public float miny;
    public float maxx;
    public float maxy;

    public Rectangle() {
        minx = maxx = miny = maxy = 0;
    }

    public Rectangle(Rectangle source) {
        minx = source.minx;
        miny = source.miny;
        maxx = source.maxx;
        maxy = source.maxy;
    }

    public Rectangle(float minx, float miny, float maxx, float maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public void setWithCenter(Vec2 center, float width, float height) {
        if(width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        minx = center.x - width / 2;
        maxx = minx + width;
        miny = center.y - height / 2;
        maxy = miny + height;
    }

    public float width() {
        return maxx - minx;
    }

    public float height() {
        return maxy - miny;
    }

    public Vec2 center() {
        return new Vec2((minx + maxx) * 0.5f, (miny + maxy) * 0.5f);
    }

    public float area() {
        return (maxx - minx) * (maxy - miny);
    }

    public boolean containsClosed(Vec2 point) {
        return point.x >= minx && point.x <= maxx && point.y >= miny && point.y <= maxy;
    }

    public boolean containsOpen(Vec2 point) {
        return point.x > minx && point.x < maxx && point.y > miny && point.y < maxy;
    }

    // resize rectangle, keeping center
    public Rectangle scale(float factor) {
        if(factor < 0) {
            throw new IllegalArgumentException();
        }
        float newWidth = width() * factor;
        float newHeight = height() * factor;
        Vec2 center = center();
        minx = center.x - newWidth * 0.5f;
        maxx = center.x + newWidth * 0.5f;
        miny = center.y - newHeight * 0.5f;
        maxy = center.y + newHeight * 0.5f;
        return this;
    }

    public static Rectangle bound(Iterable<Vec2> points) {
        Iterator<Vec2> pointsIt = points.iterator();
        if(!pointsIt.hasNext()) {
            throw new IllegalArgumentException();
        }
        Vec2 p = pointsIt.next();
        Rectangle r = new Rectangle(p.x, p.y, p.x, p.y);
        while(pointsIt.hasNext()) {
            p = pointsIt.next();
            r.minx = Math.min(r.minx, p.x);
            r.maxx = Math.max(r.maxx, p.x);
            r.miny = Math.min(r.miny, p.y);
            r.maxy = Math.max(r.maxy, p.y);
        }
        return r;
    }
}
