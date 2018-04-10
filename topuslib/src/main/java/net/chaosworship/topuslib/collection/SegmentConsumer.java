package net.chaosworship.topuslib.collection;

import net.chaosworship.topuslib.geom2d.Vec2;


public interface SegmentConsumer {
    void addSegment(Vec2 a, Vec2 b);
}
