package net.chaosworship.topuslib.random;

import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.Random;


public class SuperRandom extends Random {

    public SuperRandom() {
        super();
    }

    public float approximateGaussianFloat(float mean, float stddev, int samples) {
        float x = 0;
        for(int i = 0; i < samples; i++) {
            x += nextFloat();
        }
        final float oneOverSqrt = (float)(1.0 / Math.sqrt(samples / 3.0));
        x = x * 2 * oneOverSqrt - samples * oneOverSqrt;
        return mean + x * stddev;
    }

    public void uniformUnit(Vec2 v) {
        v.setUnit(nextDouble() * Math.PI * 2);
    }

    public void uniformInCircle(Vec2 v, float radius) {
        uniformUnit(v);
        v.scale(radius * (float)Math.sqrt(nextDouble()));
    }
}
