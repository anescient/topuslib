package net.chaosworship.topuslib.random;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


@SuppressWarnings("WeakerAccess")
public class SuperRandom extends Random {

    private static SuperRandom sGlobalInstance = null;

    public SuperRandom() {
        super();
    }

    public SuperRandom(long seed) {
        super(seed);
    }

    public static SuperRandom getInstance() {
        if(sGlobalInstance == null) {
            sGlobalInstance = new SuperRandom();
        }
        return sGlobalInstance;
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

    public float uniformInRange(float lower, float upper) {
        return lower + (upper - lower) * nextFloat();
    }

    public <E> void shuffle(E[] array) {
        for(int i = 0; i < array.length - 1; i++) {
            int j = i + nextInt(array.length - i);
            E temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public void shuffle(int[] array) {
        for(int i = 0; i < array.length - 1; i++) {
            int j = i + nextInt(array.length - i);
            int temp = array[i];
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

    public <E> E choice(Collection<E> collection) {
        if(collection.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int i = nextInt(collection.size());
        Iterator<E> iterator = collection.iterator();
        while(i-- > 0) {
            iterator.next();
        }
        return iterator.next();
    }
}
