package net.chaosworship.topuslib.geom2d;


import java.util.ArrayList;

public class Circle {

    private static final float CIRCLEOVER3 = (float)(2 * Math.PI / 3);

    public Vec2 center;
    public float radius;

    public Circle() {
        center = new Vec2(0, 0);
        radius = 0;
    }

    public Circle(Vec2 center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean contains(Vec2 p) {
        return Vec2.distanceSq(center, p) <= radius * radius;
    }

    public ArrayList<Vec2> getBoundPoints(int n) {
        if(n < 0) {
            throw new IllegalArgumentException();
        }
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            double a = (double)i * 2 * Math.PI / n;
            points.add(Vec2.unit(a).scale(radius).add(center));
        }
        return points;
    }

    // generate an equilateral triangle that tightly fits inside the circle
    public Triangle getInscribedTriangle() {
        return new Triangle(
                Vec2.unit(0).scale(radius).add(center),
                Vec2.unit(CIRCLEOVER3).scale(radius).add(center),
                Vec2.unit(2 * CIRCLEOVER3).scale(radius).add(center));
    }

    // generate an equilateral triangle that completely encloses the circle
    // if scale > 1, triangle is larger
    public Triangle getBoundingTriangle(float scale) {
        final float r = 2 * radius * scale;
        return new Triangle(
                Vec2.unit(0).scale(r).add(center),
                Vec2.unit(CIRCLEOVER3).scale(r).add(center),
                Vec2.unit(2 * CIRCLEOVER3).scale(r).add(center));
    }

    public Triangle getBoundingTriangle() {
        return getBoundingTriangle(1);
    }
}
