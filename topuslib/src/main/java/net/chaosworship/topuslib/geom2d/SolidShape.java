package net.chaosworship.topuslib.geom2d;


public interface SolidShape {

    boolean contains(Vec2 point);

    float area();

    Rectangle getBoundingRectangle();
}
