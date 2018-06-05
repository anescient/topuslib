package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


@SuppressWarnings({"unused", "WeakerAccess"})
public class Cuboid {

    public float minx;
    public float maxx;
    public float miny;
    public float maxy;
    public float minz;
    public float maxz;

    public Cuboid() {
        minx = maxx = miny = maxy = minz = maxz = 0;
    }

    public Cuboid(Cuboid source) {
        minx = source.minx;
        maxx = source.maxx;
        miny = source.miny;
        maxy = source.maxy;
        minz = source.minz;
        maxz = source.maxz;
    }

    public Cuboid(float minx, float maxx, float miny, float maxy, float minz, float maxz) {
        if(maxx < minx || maxy < miny || maxz < minz) {
            throw new IllegalArgumentException();
        }
        this.minx = minx;
        this.maxx = maxx;
        this.miny = miny;
        this.maxy = maxy;
        this.minz = minz;
        this.maxz = maxz;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Cuboid (%f,%f,%f)-(%f,%f,%f)", minx, miny, minz, maxx, maxy, maxz);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        Cuboid rhsCuboid = (Cuboid)rhs;
        return minx == rhsCuboid.minx &&
                maxx == rhsCuboid.maxx &&
                miny == rhsCuboid.miny &&
                maxy == rhsCuboid.maxy &&
                minz == rhsCuboid.minz &&
                maxz == rhsCuboid.maxz;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(minx)
                ^ Float.floatToIntBits(maxx)
                ^ Float.floatToIntBits(miny)
                ^ Float.floatToIntBits(maxy)
                ^ Float.floatToIntBits(minz)
                ^ Float.floatToIntBits(maxz);
    }

    public void set(Cuboid source) {
        minx = source.minx;
        maxx = source.maxx;
        miny = source.miny;
        maxy = source.maxy;
        minz = source.minz;
        maxz = source.maxz;
    }

    public void setWithCenter(Vec3 center, float width, float height, float depth) {
        if(width < 0 || height < 0 || depth < 0) {
            throw new IllegalArgumentException();
        }
        minx = center.x - width / 2;
        maxx = minx + width;
        miny = center.y - height / 2;
        maxy = miny + height;
        minz = center.z - depth / 2;
        maxz = minz + depth;
    }

    public boolean isDegenerate() {
        return minx == maxx || miny == maxy || minz == maxz;
    }

    public float width() {
        return maxx - minx;
    }

    public float height() {
        return maxy - miny;
    }

    public float depth() {
        return maxz - minz;
    }

    public Vec3 center() {
        return new Vec3(centerX(), centerY(), centerZ());
    }

    public float centerX() {
        return (minx + maxx) * 0.5f;
    }

    public float centerY() {
        return (miny + maxy) * 0.5f;
    }

    public float centerZ() {
        return (minz + maxz) * 0.5f;
    }

    public boolean containsClosed(Vec3 point) {
        return point.x >= minx && point.x <= maxx &&
                point.y >= miny && point.y <= maxy &&
                point.z >= minz && point.z <= maxz;
    }

    public boolean containsOpen(Vec3 point) {
        return point.x > minx && point.x < maxx &&
                point.y > miny && point.y < maxy &&
                point.z > minz && point.z < maxz;
    }


    public static Cuboid bound(Vec3 point, Vec3 ...more) {
        ArrayList<Vec3> points = new ArrayList<>();
        points.add(point);
        points.addAll(Arrays.asList(more));
        return bound(points);
    }

    public static Cuboid bound(Iterable<Vec3> points) {
        Iterator<Vec3> pointsIt = points.iterator();
        if(!pointsIt.hasNext()) {
            throw new IllegalArgumentException();
        }
        Vec3 p = pointsIt.next();
        Cuboid c = new Cuboid(p.x, p.x, p.y, p.y, p.z, p.z);
        while(pointsIt.hasNext()) {
            p = pointsIt.next();
            c.minx = Math.min(c.minx, p.x);
            c.maxx = Math.max(c.maxx, p.x);
            c.miny = Math.min(c.miny, p.y);
            c.maxy = Math.max(c.maxy, p.y);
            c.minz = Math.min(c.minz, p.z);
            c.maxz = Math.max(c.maxz, p.z);
        }
        return c;
    }
}
