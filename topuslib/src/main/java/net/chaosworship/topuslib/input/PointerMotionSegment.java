package net.chaosworship.topuslib.input;

import net.chaosworship.topuslib.math.Vec2;
import net.chaosworship.topuslib.math.Vec2Transformer;


public class PointerMotionSegment {
    private final Vec2 mStart;
    private final Vec2 mEnd;
    private final float mSeconds;

    PointerMotionSegment(Vec2 start, Vec2 end, long milliseconds) {
        this(start, end, (float)milliseconds / 1000.0f);
    }

    private PointerMotionSegment(Vec2 start, Vec2 end, float seconds) {
        mStart = start;
        mEnd = end;
        mSeconds = seconds;
    }

    public Vec2 getStart() {
        return mStart;
    }

    public Vec2 getEnd() {
        return mEnd;
    }

    public PointerMotionSegment transform(Vec2Transformer transformer) {
        return new PointerMotionSegment(
                transformer.transform(mStart),
                transformer.transform(mEnd),
                mSeconds);
    }

    public Vec2 velocity() {
        if(mSeconds > 0) {
            return Vec2.difference(mEnd, mStart).scaleInverse(mSeconds);
        } else {
            return new Vec2();
        }
    }
}
