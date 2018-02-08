package net.chaosworship.topuslib.math;


public class Rectangle {

    public float minx;
    public float miny;
    public float maxx;
    public float maxy;

    public Rectangle() {
        minx = maxx = miny = maxy = 0;
    }

    public Rectangle(float minx, float miny, float maxx, float maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public void setWithCenter(Vec2 center, float width, float height) {
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

    public boolean containsClosed(Vec2 point) {
        return point.x >= minx && point.x <= maxx && point.y >= miny && point.y <= maxy;
    }

    public boolean containsOpen(Vec2 point) {
        return point.x > minx && point.x < maxx && point.y > miny && point.y < maxy;
    }
}
