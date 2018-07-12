package net.chaosworship.topuslib.tuple;

import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.Comparator;


public class PointObjectPair {

    public static final Comparator<PointObjectPair> compareXY = new Comparator<PointObjectPair>() {
        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        @Override
        public int compare(PointObjectPair a, PointObjectPair b) {
            return a.point.lessThanXY(b.point) ? -1 : 1;
        }
    };

    public static final Comparator<PointObjectPair> compareYX = new Comparator<PointObjectPair>() {
        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        @Override
        public int compare(PointObjectPair a, PointObjectPair b) {
            return a.point.lessThanYX(b.point) ? -1 : 1;
        }
    };

    public final Vec2 point;
    public final Object value;

    public PointObjectPair(Vec2 point, Object value) {
        this.point = point;
        this.value = value;
    }

}
