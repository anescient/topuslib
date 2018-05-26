package net.chaosworship.topuslib.collection;

import net.chaosworship.topuslib.geom3d.Vec3;


public interface TriangleConsumer {
    void addTriangle(Vec3 a, Vec3 b, Vec3 c);
}
