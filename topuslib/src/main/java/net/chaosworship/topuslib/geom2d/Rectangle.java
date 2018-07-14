package net.chaosworship.topuslib.geom2d;

import android.annotation.SuppressLint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Rectangle implements SolidShape {

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

    public Rectangle(RectF source) {
        minx = source.left;
        miny = source.top;
        maxx = source.right;
        maxy = source.bottom;
    }

    public Rectangle(float minx, float miny, float maxx, float maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public Rectangle(Vec2 cornerA, Vec2 cornerB) {
        this.minx = Math.min(cornerA.x, cornerB.x);
        this.miny = Math.min(cornerA.y, cornerB.y);
        this.maxx = Math.max(cornerA.x, cornerB.x);
        this.maxy = Math.max(cornerA.y, cornerB.y);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Rectangle (%f,%f)-(%f,%f)", minx, miny, maxx, maxy);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        Rectangle rhsRect = (Rectangle)rhs;
        return minx == rhsRect.minx &&
                miny == rhsRect.miny &&
                maxx == rhsRect.maxx &&
                maxy == rhsRect.maxy;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(minx)
                ^ Float.floatToIntBits(miny)
                ^ Float.floatToIntBits(maxx)
                ^ Float.floatToIntBits(maxy);
    }

    public void set(Rectangle source) {
        minx = source.minx;
        miny = source.miny;
        maxx = source.maxx;
        maxy = source.maxy;
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

    public RectF asRectF() {
        return new RectF(minx, miny, maxx, maxy);
    }

    public boolean isDegenerate() {
        return minx == maxx || miny == maxy;
    }

    public float width() {
        return maxx - minx;
    }

    public float height() {
        return maxy - miny;
    }

    public Vec2 center() {
        return new Vec2(centerX(), centerY());
    }

    public float centerX() {
        return (minx + maxx) * 0.5f;
    }

    public float centerY() {
        return (miny + maxy) * 0.5f;
    }

    @Override
    public float area() {
        return (maxx - minx) * (maxy - miny);
    }

    public boolean containsClosed(Vec2 point) {
        return point.x >= minx && point.x <= maxx && point.y >= miny && point.y <= maxy;
    }

    public boolean containsOpen(Vec2 point) {
        return point.x > minx && point.x < maxx && point.y > miny && point.y < maxy;
    }

    public boolean containsTiled(Vec2 point) {
        return point.x >= minx && point.x < maxx && point.y >= miny && point.y < maxy;
    }

    @Override
    public boolean contains(Vec2 point) {
        return containsClosed(point);
    }

    public boolean contains(Circle circle) {
        return circle.center.x - circle.radius >= minx &&
                circle.center.x + circle.radius <= maxx &&
                circle.center.y - circle.radius >= miny &&
                circle.center.y + circle.radius <= maxy;
    }

    public boolean overlapsOpen(Rectangle other) {
        return minx < other.maxx && maxx > other.minx && maxy > other.miny && miny < other.maxy;
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return new Rectangle(this);
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

    // make a rectangle with the same center which has width + 2 * margin, height + 2 * margin
    public Rectangle enlarged(float margin) {
        return new Rectangle(
                minx - margin,
                miny - margin,
                maxx + margin,
                maxy + margin);
    }

    public Rectangle enlargedToSquare() {
        Rectangle square = new Rectangle();
        if(width() > height()) {
            square.setWithCenter(center(), width(), width());
        } else {
            square.setWithCenter(center(), height(), height());
        }
        return square;
    }

    public static Rectangle bound(Vec2 point, Vec2 ...more) {
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(point);
        points.addAll(Arrays.asList(more));
        return bound(points);
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

    public static Rectangle squareBound(Iterable<Vec2> points) {
        Rectangle rect = bound(points);
        float d = (rect.width() - rect.height()) * 0.5f;
        if(d > 0) {
            rect.miny -= d;
            rect.maxy += d;
        } else {
            rect.minx += d;
            rect.maxx -= d;
        }
        return rect;
    }
}
