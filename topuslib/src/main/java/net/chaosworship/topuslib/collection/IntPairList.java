package net.chaosworship.topuslib.collection;

import java.util.Arrays;


public class IntPairList implements IntPairConsumer {

    private int[] mData;
    private int mPairCount;

    public IntPairList() {
        mData = new int[20];
        mPairCount = 0;
    }

    public void add(int a, int b) {
        int intCount = mPairCount * 2;
        if(mData.length < intCount + 2) {
            mData = Arrays.copyOf(mData, 2 * (((mData.length / 2) * 3 / 2) + 1));
        }
        mData[intCount] = a;
        mData[intCount + 1] = b;
        mPairCount++;
    }

    public int getPairCount() {
        return mPairCount;
    }

    public void clear() {
        mPairCount = 0;
    }

    @Override
    public void addIntPair(int a, int b) {
        add(a, b);
    }

    public void outputPairs(IntPairConsumer consumer) {
        int intCount = mPairCount * 2;
        for(int i = 0; i < intCount;) {
            consumer.addIntPair(mData[i++], mData[i++]);
        }
    }
}
