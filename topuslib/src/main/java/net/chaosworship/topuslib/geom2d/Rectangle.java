package net.chaosworship.topuslib.geom2d;


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
}
