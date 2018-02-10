package net.chaosworship.topuslib.math.rangesearch;

import net.chaosworship.topuslib.math.Rectangle;
import net.chaosworship.topuslib.math.Vec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class kdTree<T> implements RectangularSearch<T> {

    //////////////////////////////////////////////////

    private interface Node<T> {

        // add all values with points in area to the given collection
        void search(Rectangle area, Collection<T> searchResults);

        // add entire subtree's values to the given collection
        void getAll(Collection<T> valuesCollection);
    }

    //////////////////////////////////////////////////

    private static class InternalNode<T> implements Node<T> {

        private final ArrayList<Node<T>> mChildren;

        InternalNode() {
            mChildren = new ArrayList<>();
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {

        }

        @Override
        public void getAll(Collection<T> valuesCollection) {
            for(Node<T> c : mChildren) {
                c.getAll(valuesCollection);
            }
        }
    }

    //////////////////////////////////////////////////

    private static class LeafNode<T> implements Node<T> {
        private Vec2 mPoint;
        private T mValue;

        LeafNode() {
            mPoint = null;
            mValue = null;
        }

        void setValue(Vec2 point, T value) {
            mPoint = point;
            mValue = value;
        }

        @Override
        public void search(Rectangle area, Collection<T> searchResults) {
            if(area.containsClosed(mPoint)) {
                searchResults.add(mValue);
            }
        }

        @Override
        public void getAll(Collection<T> valuesCollection) {
            valuesCollection.add(mValue);
        }
    }

    //////////////////////////////////////////////////

    private Node<T> mRoot;
    private final ArrayList<PointValuePair<T>> mPointValuesByX;
    private final ArrayList<PointValuePair<T>> mPointValuesByY;
    private final ArrayList<T> mSearchResults;

    public kdTree() {
        mRoot = null;
        mPointValuesByX = new ArrayList<>();
        mPointValuesByY = new ArrayList<>();
        mSearchResults = new ArrayList<>();
    }

    @Override
    public void load(Collection<PointValuePair<T>> pointValues) {
        mPointValuesByX.clear();
        mPointValuesByX.addAll(pointValues);
        mPointValuesByY.clear();
        mPointValuesByY.addAll(pointValues);
        reload();
    }

    // if the collection of points/values objects hasn't changed but the points themselves have been altered
    public void reload() {
        Collections.sort(mPointValuesByX, PointValuePair.compareXY);
        Collections.sort(mPointValuesByY, PointValuePair.compareYX);
        mRoot = null;
    }

    @Override
    public Iterable<T> search(Rectangle area) {
        mSearchResults.clear();
        if(mRoot != null) {

        }
        return mSearchResults;
    }
}
