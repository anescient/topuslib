package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Vec2;

import java.util.Comparator;


public class PointValuePair<T> {

    static final Comparator<PointValuePair> compareXY = new Comparator<PointValuePair>() {
        @Override
        public int compare(PointValuePair a, PointValuePair b) {
            return a.point.lessThanXY(b.point) ? -1 : 1;
        }
    };

    static final Comparator<PointValuePair> compareYX = new Comparator<PointValuePair>() {
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
