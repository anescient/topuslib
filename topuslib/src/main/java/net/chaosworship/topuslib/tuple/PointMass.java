package net.chaosworship.topuslib.tuple;

import net.chaosworship.topuslib.geom2d.Vec2;


public class PointMass {

    public Vec2 position;
    public float mass;

    public PointMass() {
        position = new Vec2();
        mass = 0;
    }

    public PointMass(Vec2 position, float mass) {
        this.position = position;
        this.mass = mass;
    }
}
