package net.chaosworship.topuslib.random;

import java.util.ArrayList;
import java.util.Random;


public class WeightedSampler<V> {

    private class WeightedItem<V> {
        private final float mWeight;
        private final V mItem;

        public WeightedItem(float weight, V item) {
            mWeight = weight;
            mItem = item;
        }

        public float getWeight() {
            return mWeight;
        }

        public V getItem() {
            return mItem;
        }
    }

    private final ArrayList<WeightedItem<V>> mItems;

    public WeightedSampler() {
        mItems = new ArrayList<>();
    }

    public void insert(Float weight, V item) {
        mItems.add(new WeightedItem<V>(weight, item));
    }

    public V sigmaScaledSample(Random random)
            throws Exception {
        if(mItems.isEmpty())
            throw new Exception();

        // w = max(w - w_mean + 2 * w_stddev, 0)
        float weightMean = 0;
        for(WeightedItem<V> wi : mItems) {
            weightMean += wi.getWeight();
        }
        weightMean /= mItems.size();

        float weightStddev = 0;
        for(WeightedItem<V> wi : mItems) {
            float meandif = wi.getWeight() - weightMean;
            weightStddev += meandif * meandif;
        }
        weightStddev = (float)Math.sqrt(weightStddev / mItems.size());

        float[] scaledWeights = new float[mItems.size()];
        float scaledWeightsSum = 0;
        for(int i = 0; i < mItems.size(); i++) {
            float w = mItems.get(i).getWeight() - weightMean + 1 * weightStddev;
            w = w > 0 ? w : 0;
            scaledWeights[i] = w;
            scaledWeightsSum += w;
        }

        float x = random.nextFloat() * scaledWeightsSum;
        int i = 0;
        while(i < mItems.size() - 1 && x > scaledWeights[i])
            x -= scaledWeights[i++];
        return mItems.get(i).getItem();
    }
}
