package net.chaosworship.topuslib.tuple;

import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.Comparator;


public class PointValuePair<T> {

    public static final Comparator<PointValuePair> compareXY = new Comparator<PointValuePair>() {
        @Override
        public int compare(PointValuePair a, PointValuePair b) {
            return a.point.lessThanXY(b.point) ? -1 : 1;
        }
    };

    public static final Comparator<PointValuePair> compareYX = new Comparator<PointValuePair>() {
        @Override
        public int compare(PointValuePair a, PointValuePair b) {
            return a.point.lessThanYX(b.point) ? -1 : 1;
        }
    };

    public final Vec2 point;
    public final T value;

    public PointValuePair(Vec2 point, T value) {
        this.point = point;
        this.value = value;
    }
}
