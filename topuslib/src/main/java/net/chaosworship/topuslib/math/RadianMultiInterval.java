package net.chaosworship.topuslib.math;

import net.chaosworship.topuslib.geom2d.Arc;
import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.ArrayList;
import java.util.Random;


public class RadianMultiInterval {

    ////////////////////////////////////////

    private static class RadianRange {
        private final double low;
        private final double high;
        private final double length;
        RadianRange(double low, double high) {
            if(low > high) {
                throw new IllegalArgumentException();
            }
            if(low < 0 || low > TWOPI || high < 0 || high > TWOPI) {
                throw new IllegalArgumentException();
            }
            this.low = low;
            this.high = high;
            length = this.high - this.low;
        }

        private boolean contains(double x) {
            return x >= this.low && x < this.high;
        }

        private boolean contains(RadianRange range) {
            return this.low <= range.low && this.high >= range.high;
        }
    }

    ////////////////////////////////////////

    private static final double TWOPI = 2 * Math.PI;

    private final ArrayList<RadianRange> mRanges;

    public RadianMultiInterval() {
        mRanges = new ArrayList<>();
        setFullCircle();
    }

    public boolean isEmpty() {
        return mRanges.isEmpty();
    }

    public void clear() {
        mRanges.clear();
    }

    public void setFullCircle() {
        mRanges.clear();
        mRanges.add(new RadianRange(0, TWOPI));
    }

    public ArrayList<Arc> getArcs(Circle circle) {
        ArrayList<Arc> arcs = new ArrayList<>();
        for(RadianRange rr : mRanges) {
            arcs.add(new Arc(circle, rr.low, rr.high));
        }
        return arcs;
    }

    public double getTotalRadians() {
        double radians = 0;
        for(RadianRange rr : mRanges) {
            radians += rr.length;
        }
        return radians;
    }

    public boolean includes(double a) {
        a = Angles.unloopRadians(a);
        for(RadianRange rr : mRanges) {
            if(rr.contains(a)) {
                return true;
            }
        }
        return false;
    }

    public void exclude(double low, double high) {
        if(high < low) {
            throw new IllegalArgumentException();
        }
        if(high == low || mRanges.isEmpty()) {
            return;
        }
        if(high - low >= TWOPI) {
            clear();
            return;
        }
        if(low < 0 || low >= TWOPI) {
            double unturn = Math.floor(low / TWOPI) * TWOPI;
            low -= unturn;
            high -= unturn;
        }
        if(high > TWOPI) {
            exclude(0, high - TWOPI);
            high = TWOPI;
        }
        ArrayList<RadianRange> newRanges = new ArrayList<>();
        for(RadianRange rr : mRanges) {
            if(rr.high <= low || rr.low >= high) {
                newRanges.add(rr);
            } else {
                if(low >= rr.low) {
                    newRanges.add(new RadianRange(rr.low, low));
                }
                if(rr.high >= high) {
                    newRanges.add(new RadianRange(high, rr.high));
                }
            }
        }
        mRanges.clear();
        mRanges.addAll(newRanges);
    }

    public double uniformSample(Random random) {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        double x = random.nextDouble() * getTotalRadians();
        for(RadianRange rr : mRanges) {
            if(x > rr.length) {
                x -= rr.length;
                continue;
            }
            return rr.low + x;
        }

        // very exceptional case
        return mRanges.get(mRanges.size() - 1).high;
    }
}
