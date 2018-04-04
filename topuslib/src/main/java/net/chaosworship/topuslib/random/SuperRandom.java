package net.chaosworship.topuslib.random;

import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.List;
import java.util.Random;


@SuppressWarnings("WeakerAccess")
public class SuperRandom extends Random {

    public SuperRandom() {
        super();
    }

    public SuperRandom(long seed) {
        super(seed);
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

    public Vec2 uniformUnit() {
        Vec2 v = new Vec2();
        uniformUnit(v);
        return v;
    }

    public void uniformInCircle(Vec2 v, float radius) {
        uniformUnit(v);
        v.scale(radius * (float)Math.sqrt(nextDouble()));
    }

    public <E> void shuffle(E[] array) {
        for(int i = 0; i < array.length - 1; i++) {
            int j = i + nextInt(array.length - i);
            E temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public <E> void subShuffle(E[] array, int skip) {
        subShuffle(array, skip, array.length - skip);
    }

    public <E> void subShuffle(E[] array, int skip, int count) {
        int limit = skip + count;
        for(int i = skip; i < limit - 1; i++) {
            int j = i + nextInt(limit - i);
            E temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public <E> void shuffle(List<E> list) {
        int n = list.size();
        for(int i = 0; i < n - 1; i++) {
            int j = i + nextInt(n - i);
            E temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    public <E> E choice(List<E> list) {
        if(list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return list.get(nextInt(list.size()));
    }
}
