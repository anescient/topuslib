package net.chaosworship.topuslib.random;

import java.util.ArrayList;
import java.util.Random;


public class RandomQueue<T> {

    private final Random mRandom;
    private final ArrayList<T> mItems;

    public RandomQueue() {
        this(new Random());
    }

    public RandomQueue(Random random) {
        mRandom = random;
        mItems = new ArrayList<>();
    }

    public int size() {
        return mItems.size();
    }

    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public void clear() {
        mItems.clear();
    }

    public void add(T item) {
        mItems.add(item);
    }

    public T popRandom() {
        if(mItems.isEmpty()) {
            throw new IllegalStateException();
        }
        int i = mRandom.nextInt(mItems.size());
        T got = mItems.get(i);
        int lasti = mItems.size() - 1;
        mItems.set(i, mItems.get(lasti));
        mItems.remove(lasti);
        return got;
    }
}
